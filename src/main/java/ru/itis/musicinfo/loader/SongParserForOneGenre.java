package ru.itis.musicinfo.loader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.musicinfo.dao.AuthorDAO;
import ru.itis.musicinfo.dao.GenreDAO;
import ru.itis.musicinfo.dao.MusicInfoDAO;
import ru.itis.musicinfo.dao_mysql.AuthorDaoMysql;
import ru.itis.musicinfo.dao_mysql.GenreDaoMysql;
import ru.itis.musicinfo.dao_mysql.MusicInfoDaoMysql;
import ru.itis.musicinfo.exceptions.MusicInfoException;
import ru.itis.musicinfo.model.Author;
import ru.itis.musicinfo.model.Genre;
import ru.itis.musicinfo.model.MusicInfo;
import ru.itis.musicinfo.utils.DataUtils;

import javax.xml.crypto.Data;
import java.io.IOException;

public class SongParserForOneGenre {
    private static final Logger logger = LoggerFactory.getLogger(SongParserForOneGenre.class);

    private AuthorDAO authorDAO;
    private MusicInfoDAO musicInfoDAO;
    private Long genreId;

    public void setGenreId(Long genreId) {
        this.genreId = genreId;
    }

    public SongParserForOneGenre(Long genreId) {
        this.genreId = genreId;
        try {
            authorDAO = new AuthorDaoMysql();
            musicInfoDAO = new MusicInfoDaoMysql();
        } catch (MusicInfoException e) {
            logger.error("Constructor exception DAO", e);
            try {
                if (authorDAO != null) authorDAO.close();
                if (musicInfoDAO != null) musicInfoDAO.close();
            } catch (Exception ex) {
                logger.error("Can not close DAO", ex);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        SongParserForOneGenre test = new SongParserForOneGenre(10L);
        test.pasrePage("http://muzoton.ru/1393-2517-50-gramm.html");

    }

    public void pasrePage(String url) throws IOException {
        //Connect with page on url address and parse songtext section
        Document doc = Jsoup.connect(url).get();

        //Extract song author and genre
        Elements info = doc.select("div.genre").select("a[href]");
        //String author = info.get(1).text();

        //Extract song name
        info = doc.select("h1");
        Element elem = info.select("a[href]").first();
        String authorName = "";
        if (elem != null) {
            authorName = elem.text();
        }


        info.select("a[href]").remove();
        info.select("span").remove();

        String songName = info.text();
        if(DataUtils.isEmpty(songName)){
            return;
        }
        int i = 0;
        while (i < songName.length() && !Character.isLetter(songName.charAt(i)) && !Character.isDigit(songName.charAt(i))) {
            i++;
        }
        songName = songName.substring(i);

        //Extract text and delete translate section
        info = doc.select("div.songtext");
        info.select("h3").remove();
        info.select("div").remove();

        //Get text and delete unnecessary rap song strings
        String songText = info.text();
        songText = songText.replaceAll("\\[.*?\\] ?", "");

        //Save song and associated with it genre and author
        if (!(DataUtils.isEmpty(songName) || DataUtils.isEmpty(songText))){
            Long authorId = null;
            if (!DataUtils.isEmpty(authorName)) {
                Author author = authorDAO.get(authorName);
                if (author == null) {
                    authorId = authorDAO.create(authorName);
                } else {
                    authorId = author.getId();
                }
            }
            try {
                MusicInfo musicInfo = new MusicInfo(songName, genreId);
                if (authorId!=null){
                    musicInfo.setAuthor_id(authorId);
                }
                musicInfo.setText(songText);
                musicInfo.setUrl_address(url);
                musicInfoDAO.create(musicInfo);
            } catch (Throwable exDublicate) {
                logger.debug(songText + " exists");
            }
        }
    }
}
