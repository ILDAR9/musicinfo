package ru.itis.musicinfo.dao_mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.musicinfo.dao.AuthorDAO;
import ru.itis.musicinfo.dao_mysql.util.CreateConnection;
import ru.itis.musicinfo.exceptions.MusicInfoException;
import ru.itis.musicinfo.model.Author;
import ru.itis.musicinfo.model.MusicInfo;
import ru.itis.musicinfo.utils.DataUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implemented by Mysql
 */
public class AuthorDaoMysql extends CreateConnection implements AuthorDAO {
    private static final Logger logger = LoggerFactory.getLogger(AuthorDaoMysql.class);
    private static final String sqlEmplPropertiesFileName = "/mysql_author.properties";

    private static final int CREATE_AUTHOR = 0,
            GET_SOUND_COUNT_OF_AUTHOR = 1,
            GET_MUSIC_INFO_LIST_OF_AUTHOR = 2,
            GET_AUTHOR = 3;

    public AuthorDaoMysql() throws MusicInfoException {
        super();
        initilizePrepStatements(sqlEmplPropertiesFileName, "mysql.author.create", "mysql.author.get_sound_count", "mysql.author.get_musicinfolist", "mysql.author.get_author");
    }

    public Long create(String name) {
        boolean isCreated = false;
        Long author_id = -1L;
        if (DataUtils.isEmpty(name)) {
            logger.warn("Contains null fields");
        } else
            try {
                prepSt = prepStatements[CREATE_AUTHOR];
                prepSt.setString(1, name.toLowerCase());
                isCreated = prepSt.executeUpdate() > 0;
                if (isCreated) {
                    logger.debug("author: " + name + " was created");
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.error("while trying to create new Author", ex);
            }
        if (isCreated) {
            author_id = get(name).getId();
        }
        return author_id;
    }

    public Author get(String name) {
        Author author = null;
        try {
            prepSt = prepStatements[GET_AUTHOR];
            prepSt.clearParameters();
            prepSt.setString(1, name.toLowerCase());
            try (ResultSet rs = prepSt.executeQuery()) {
                if (rs.next()) {
                    author = new Author(rs.getString("AUTHOR_NAME"));
                    author.setId(rs.getLong("AUTHOR_ID"));
                }
            }
        } catch (SQLException ex) {
            logger.error("while trying to create new Author", ex);
        }
        return author;
    }

    public int getSoundCount(Author author) {
        int count = 0;
        try {
            prepSt = prepStatements[GET_SOUND_COUNT_OF_AUTHOR];
            prepSt.setLong(1, author.getId());
            ResultSet rs = prepSt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("AUTHOR_SONG_COUNT");
            }
        } catch (SQLException ex) {
            logger.error("while trying to create new Author", ex);
        }
        return count;
    }

    public List<MusicInfo> getMusicList(Author author) {
        List<MusicInfo> musicInfoList = new ArrayList<>();
        try {
            prepSt = prepStatements[GET_MUSIC_INFO_LIST_OF_AUTHOR];
            prepSt.setLong(1, author.getId());
            ResultSet rs = prepSt.executeQuery();
            while (rs.next()) {
                MusicInfo musicInfo = new MusicInfo(rs.getString("MINFO_NAME"), rs.getLong("MINFO_GENRE_ID"));
                musicInfo.setId(rs.getLong("MINFO_ID"));
                musicInfo.setAuthor_id(rs.getLong("MINFO_AUTHOR_ID"));
                musicInfo.setText(rs.getString("MINFO_TEXT"));
                musicInfo.setUrl_address(rs.getString("MINFO_URL"));
                musicInfoList.add(musicInfo);
            }

        } catch (SQLException ex) {
            logger.error("while trying to create new Author", ex);
        }

        return musicInfoList;
    }


}
