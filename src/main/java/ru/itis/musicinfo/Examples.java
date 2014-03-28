package ru.itis.musicinfo;

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


/**
 * Test class
 */
public class Examples {
    private static final Logger logger = LoggerFactory.getLogger(Examples.class);

    public static void main(String[] args) throws Exception {
        musicInfoTest();
    }

    static void authorTest() throws Exception {
        AuthorDAO authorDao = new AuthorDaoMysql();
        Author author = authorDao.get("ildar");
        System.out.println(author);
        System.out.println(authorDao.getSoundCount(author));
        System.out.println(authorDao.getMusicList(author));
    }

    static void genreTest() throws Exception {
        GenreDAO genreDAO = new GenreDaoMysql();
        Genre genre = genreDAO.get("Rock");
        System.out.println(genre);
        System.out.println(genreDAO.getMusicCount(genre));
        System.out.println(genreDAO.getMusicList(genre));
    }

    static void musicInfoTest() throws Exception {
        //Your data
        String authorName = "Linkin Park".toLowerCase();
        String genreName = "POP-Rock".toLowerCase();
        String songName = "No war!";
        String text = "Hey hey I wanna love, no WAR!";


        //Code for creation MusicInfo row in DB
        Long genreId = -1L;
        Long authorId = null;
        try {
            if (DataUtils.isEmpty(genreName)) {
                throw new MusicInfoException("genreName is empty");
            } else {
                try (GenreDAO genreDAO = new GenreDaoMysql()) {
                    Genre genre = genreDAO.get(genreName);
                    if (genre != null) {
                        genreId = genre.getId();
                    } else {
                        genreId = genreDAO.create(genreName);
                    }
                }
            }

            if (!DataUtils.isEmpty(authorName)) {
                try (AuthorDAO authorDao = new AuthorDaoMysql()) {
                    Author author = authorDao.get(authorName);
                    if (author != null) {
                        authorId = author.getId();
                    } else {
                        authorId = authorDao.create(authorName);
                    }
                }
            }

            try (MusicInfoDAO musicInfoDAO = new MusicInfoDaoMysql()) {
                MusicInfo musicInfo = new MusicInfo(songName, genreId);
                musicInfo.setText(text);
                musicInfo.setAuthor_id(authorId);
                System.out.println(musicInfoDAO.create(musicInfo));
            }
        } catch (MusicInfoException mex) {
            logger.error("", mex);
        }

    }
}
