package ru.itis.musicinfo.dao_mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.musicinfo.dao.GenreDAO;
import ru.itis.musicinfo.dao_mysql.util.CreateConnection;
import ru.itis.musicinfo.exceptions.MusicInfoException;
import ru.itis.musicinfo.model.Genre;
import ru.itis.musicinfo.model.MusicInfo;
import ru.itis.musicinfo.utils.DataUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Implemented by Mysql
 */
public class GenreDaoMysql extends CreateConnection implements GenreDAO {
    private final static Logger logger = LoggerFactory.getLogger(GenreDaoMysql.class);
    private static final int CREATE_GENRE = 0,
            GET_GENRE_SONG_COUNT = 1,
            GET_MUSIC_INFO_LIST = 2,
            GET = 3;
    private static final String sqlEmplPropertiesFileName = "/mysql_genre.properties";


    public GenreDaoMysql() throws MusicInfoException {
        super();
        initilizePrepStatements(sqlEmplPropertiesFileName, "mysql.genre.create", "mysql.genre.get_sound_count", "mysql.genre.get_musicinfolist", "mysql.genre.get_genre");
    }

    public Long create(String name) {
        boolean isCreated = false;
        Long genre_id = -1L;
        if (DataUtils.isEmpty(name)) {
            logger.warn("Contains null fields");
        } else {
            try {
                prepSt = prepStatements[CREATE_GENRE];
                prepSt.setString(1, name.toLowerCase());
                isCreated = prepSt.executeUpdate() > 0;
                if (isCreated) {
                    logger.debug("genre: " + name + " was created");
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.error("while trying to create new GENRE", ex);
            }
        }
        if (isCreated) {
            genre_id = get(name).getId();
        }
        return genre_id;
    }

    public int getMusicCount(Genre genre) {
        int count = 0;
        try {
            prepSt = prepStatements[GET_GENRE_SONG_COUNT];
            prepSt.setLong(1, genre.getId());
            ResultSet rs = prepSt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("GENRE_SONG_COUNT");
            }
        } catch (SQLException ex) {
            logger.error("while trying get MusicInfo GENRE", ex);
        }
        return count;
    }

    public List<MusicInfo> getMusicList(Genre genre) {
        List<MusicInfo> musicInfoList = new ArrayList<>();
        try {
            prepSt = prepStatements[GET_MUSIC_INFO_LIST];
            prepSt.setLong(1, genre.getId());
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


    public Genre get(String name) {
        Genre genre = null;
        try {
            prepSt = prepStatements[GET];
            prepSt.setString(1, name.toLowerCase());
            ResultSet rs = prepSt.executeQuery();
            if (rs.next()) {
                genre = new Genre(rs.getString("GENRE_NAME"));
                genre.setId(rs.getLong("GENRE_ID"));
            }
        } catch (SQLException ex) {
            logger.error("while trying get GENRE", ex);
        }
        return genre;
    }
}
