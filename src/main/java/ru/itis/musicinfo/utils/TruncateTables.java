package ru.itis.musicinfo.utils;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

/**
 * for @After test :
 * deletes all tables
 */
public class TruncateTables {
    private static String connection_url, user, password, dbname;

    private static final String dbPropertiesFileName = "/mysql.properties";

    public TruncateTables() {
        try {
            Properties dbProperties = initilizePropMySql(dbPropertiesFileName);
            final String driver = dbProperties.getProperty("mysql.driver");
            user = dbProperties.getProperty("mysql.user");
            password = dbProperties.getProperty("mysql.password");
            final String url = dbProperties.getProperty("mysql.url");
            final String port = dbProperties.getProperty("mysql.port");
            dbname = dbProperties.getProperty("mysql.name");
            connection_url = String.format("%s:%s/%s", url, port, dbname);
            Class.forName(driver);
        } catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    private Properties initilizePropMySql(String propFileName) throws IOException {
        Properties dbProperties = new Properties();
        dbProperties.load(getClass().getResourceAsStream(propFileName));
        return dbProperties;
    }

    private static final String truncateGenreSql = "delete from GENRE";
    private static final String truncateAuthorRSql = "delete from AUTHOR";
    private static final String truncateMusicInfoSql = "delete from MUSIC_INFO";
    private static final String truncateAllOfGenre = "delete m from MUSIC_INFO m LEFT JOIN GENRE g on m.MINFO_GENRE_ID = g.GENRE_ID where g.GENRE_NAME = ?";
    private static final String deleteGenre = "delete from GENRE where GENRE_NAME = ?";

    public boolean truncateTables() {
        boolean isDeleted = false;
        try {
            try (Connection conn = DriverManager.getConnection(connection_url, user, password)) {
                try (Statement st = conn.createStatement()) {
                    isDeleted = st.executeUpdate(truncateMusicInfoSql) >= 0;
                }
                try (Statement st = conn.createStatement()) {
                    isDeleted &= st.executeUpdate(truncateAuthorRSql) >= 0;
                }
                try (Statement st = conn.createStatement()) {
                    isDeleted &= st.executeUpdate(truncateGenreSql) >= 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isDeleted;
    }


    public boolean truncateTablesOfGenre(String genreName) {
        boolean isDeleted = false;
        try {
            try (Connection conn = DriverManager.getConnection(connection_url, user, password)) {
                try (PreparedStatement prSt = conn.prepareStatement(truncateAllOfGenre)) {
                    prSt.setString(1, genreName);
                    isDeleted = prSt.executeUpdate() >= 0;
                }
                try (PreparedStatement prSt = conn.prepareStatement(deleteGenre)) {
                    prSt.setString(1, genreName);
                    isDeleted &= prSt.executeUpdate() >= 0;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return isDeleted;
    }

    public static void main(String[] args) throws Exception {
        TruncateTables tr = new TruncateTables();
        tr.truncateTables();
    }
}
