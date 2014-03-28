package ru.itis.musicinfo.dao_mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.musicinfo.dao.MusicInfoDAO;
import ru.itis.musicinfo.dao_mysql.util.CreateConnection;
import ru.itis.musicinfo.exceptions.MusicInfoException;
import ru.itis.musicinfo.model.MusicInfo;
import ru.itis.musicinfo.utils.DataUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implemented by Mysql
 */
public class MusicInfoDaoMysql extends CreateConnection implements MusicInfoDAO {
    private static final Logger logger = LoggerFactory.getLogger(MusicInfoDaoMysql.class);
    private static final String sqlEmplPropertiesFileName = "/mysql_musicinfo.properties";

    private static final int MUSIC_INFO_CREATE = 0,
            MUSIC_INFO_TOTAL_COUNT = 1,
            MUSIC_INFO_GET_BY_NAME = 2;


    public MusicInfoDaoMysql() throws MusicInfoException {
        super();
        initilizePrepStatements(sqlEmplPropertiesFileName, "mysql.musicinfo.create", "mysql.musicinfo.get_total_count", "mysql.musicinfo.get_by_name");
    }

    public boolean create(MusicInfo musicInfo) {
        boolean isCreated = false;
        if (DataUtils.isEmpty(musicInfo.getName()) && musicInfo.getGenre_id() == null && DataUtils.isEmpty(musicInfo.getText())) {
            logger.warn("Contains null fields");
        } else
            try {
                prepSt = prepStatements[MUSIC_INFO_CREATE];
                prepSt.setString(1, musicInfo.getName());
                prepSt.setString(2, musicInfo.getText());
                prepSt.setLong(3, musicInfo.getGenre_id());
                if (musicInfo.getAuthor_id() != null) {
                    prepSt.setLong(4, musicInfo.getAuthor_id());
                }

                isCreated = prepSt.executeUpdate() > 0;
                if (isCreated) {
                    logger.debug("Author was created");
                    conn.commit();
                } else {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                logger.error("while trying to create new MUSIC_INFO", ex);
            }
        return isCreated;
    }

    public int getTotalCount() {
        int count = 0;
        try {
            prepSt = prepStatements[MUSIC_INFO_TOTAL_COUNT];
            ResultSet rs = prepSt.executeQuery();
            if (rs.next()) {
                count = rs.getInt("MINFO_TOTAL_COUNT");
            }
        } catch (SQLException ex) {
            logger.error("while trying get total count MUSIC_INFO", ex);
        }
        return count;
    }
}
