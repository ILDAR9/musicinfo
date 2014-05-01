package ru.itis.musicinfo.loader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.musicinfo.dao.GenreDAO;
import ru.itis.musicinfo.dao_mysql.GenreDaoMysql;
import ru.itis.musicinfo.model.Genre;
import ru.itis.musicinfo.utils.DataUtils;
import ru.itis.musicinfo.utils.PropertiesSingelton;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class PageIterator {
    private static final String defaultUrl = PropertiesSingelton.getInstance().getProperty("default_link");
    private static final Logger logger = LoggerFactory.getLogger(PageIterator.class);

    private static final int delayTime = 1000;
    private static String PAGE_INFIX = "/page/";

    public void proceed(String... genreNames) throws IOException {
        // обработка списка жанров
        for (String genreName : genreNames) {
            proceed(genreName);
        }
    }

    private void proceed(String genreName) throws IOException {
        Long genreId = null;
        try (GenreDAO genreDao = new GenreDaoMysql()) {
            Genre genre = genreDao.get(genreName);
            if (genre != null) {
                genreId = genre.getId();
            } else {
                genreId = genreDao.create(genreName);
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        if (genreId != null && genreId > 0) {
            final int countPage = getPageCount(genreName);
            for (int i = 1; i <= countPage; i++) {
                System.out.println("------------------------------------------------ " + i + " page");
                List<String> songLinks = getSongLisFromGenrePage(defaultUrl + genreName + PAGE_INFIX + i);
                SongParserForOneGenre songParser = new SongParserForOneGenre(genreId);
                saveSongs(songLinks, songParser);
            }
        }
    }

    public int getPageCount(String genreName) throws IOException {
        int pageCount = 1;
        String url = defaultUrl + genreName;
        Document doc = Jsoup.connect(url).get();
        Elements elementLinks = doc.select("tr.navigation > td > a[href]");
        if (!DataUtils.isEmpty(elementLinks) && elementLinks.size() > 1) {
            String digit = elementLinks.get(elementLinks.size() - 2).ownText();
            try {
                pageCount = Integer.parseInt(digit);
            } catch (NumberFormatException ex) {
                logger.warn(digit, ex);
            }
        }
        return pageCount;
    }


    public List<String> getSongLisFromGenrePage(String url) throws IOException {
        List<String> songList = new LinkedList<>();
        Document doc = Jsoup.connect(url).get();
        Elements elementLinks = doc.select("tr:not(.navigation) > td > a[href]");
        for (Element element : elementLinks) {
            songList.add(element.attr("href"));
        }
        return songList;
    }

    public void saveSongs(List<String> songLinkList, SongParserForOneGenre songParser) {
        if (songParser != null) {

            for (String songLink : songLinkList) {
                if (!DataUtils.isEmpty(songLink)) {
                    try {
                        songParser.pasrePage(songLink);
                        Thread.sleep(delayTime);
                    } catch (IOException | InterruptedException e) {
                        logger.error("", e);
                    }
                }
            }
        }
    }

}
