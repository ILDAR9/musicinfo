package ru.itis.musicinfo.dao_mysql;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import ru.itis.musicinfo.dao.AuthorDAO;
import ru.itis.musicinfo.dao.GenreDAO;
import ru.itis.musicinfo.dao.MusicInfoDAO;
import ru.itis.musicinfo.model.Author;
import ru.itis.musicinfo.model.MusicInfo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MusicInfoDaoMysqlTest {

    public static AuthorDAO authorDAO;
    public static TruncateTables truncater;


    @BeforeClass
    public static void setUp() throws Exception {
        try {
            authorDAO = new AuthorDaoMysql();
            truncater = new TruncateTables();
            truncater.truncateTables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @After
    public void deleteAllRows() {
        truncater.truncateTables();
    }

    @Test
    public void createTest() {
        String name = "Ramstein";
        long idAuthor = authorDAO.create(name);
        assertTrue("Author was not created", idAuthor >= 0L);
        Author author = authorDAO.get(name);
        assertNotNull("Created author was not found", author);
    }

    @Test
    public void countTest() throws Exception {
        new TruncateTables().truncateTables();
        String[] musicNames = {"test1", "test2", "test3", "test4"};
        Long genreId = -1L;
        try (GenreDAO genreDao = new GenreDaoMysql()) {
            genreId = genreDao.create("POP");
        }
        final String authorName = "Ivan";
        Long authorId = -1L;
        try (AuthorDAO authorDAO = new AuthorDaoMysql()) {
            authorId = authorDAO.create(authorName);
        }
        try (MusicInfoDAO musicInfoDAO = new MusicInfoDaoMysql()) {
            String text = "bigText";
            for (String name : musicNames) {
                MusicInfo musicInfo = new MusicInfo(name, genreId);
                musicInfo.setAuthor_id(authorId);
                musicInfo.setText(text);
                musicInfoDAO.create(musicInfo);
            }
        }
        try (AuthorDAO authorDAO = new AuthorDaoMysql()) {
            Author author = authorDAO.get(authorName);
            assertEquals("Music count",musicNames.length, authorDAO.getSoundCount(author));
        }
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (authorDAO != null) {
            authorDAO.close();
        }
    }

}
