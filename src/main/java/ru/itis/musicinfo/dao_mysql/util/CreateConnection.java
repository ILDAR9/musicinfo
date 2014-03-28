package ru.itis.musicinfo.dao_mysql.util;

import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.musicinfo.exceptions.MusicInfoException;
import ru.itis.musicinfo.model.MusicInfo;

abstract public class CreateConnection implements AutoCloseable {

    protected Connection conn;
    protected PreparedStatement prepSt;

    /**
     * String Array of prepared statements
     */
    protected PreparedStatement[] prepStatements;


    private static final String dbPropertiesFileName = "/mysql.properties";
    private static final String createTableFileName = "/mysql_tables.properties";
    private static boolean isFirstAccess = true;

    private static String connection_url, user, password;
    /*
     * Logger
     */
    private static final Logger logger = LoggerFactory.getLogger(CreateConnection.class.getName());

    private Properties initilizePropMySql(String propFileName) throws IOException {
        Properties dbProperties = new Properties();
        dbProperties.load(getClass().getResourceAsStream(propFileName));
        return dbProperties;
    }

    /**
     * Creates one Connection entity
     *
     * @throws SQLException
     */
    protected CreateConnection() throws MusicInfoException {
        try {
            if (isFirstAccess) {
                Properties dbProperties = initilizePropMySql(dbPropertiesFileName);
                final String driver = dbProperties.getProperty("mysql.driver");
                user = dbProperties.getProperty("mysql.user");
                password = dbProperties.getProperty("mysql.password");
                final String url = dbProperties.getProperty("mysql.url");
                final String port = dbProperties.getProperty("mysql.port");
                final String dbName = dbProperties.getProperty("mysql.name");
                connection_url = String.format("%s:%s/%s", url, port, dbName);
                logger.info(connection_url);
                Class.forName(driver);
            }
            conn = DriverManager.getConnection(connection_url, user, password);
            //Auto commit is disabled
            conn.setAutoCommit(false);

            if (isFirstAccess) {
                createTable();
                isFirstAccess = false;
            }

        } catch (SQLException | IOException | ClassNotFoundException ex) {
            logger.warn("{} was caught while trying to create Connection to db", ex.getClass().getName(), ex);
            throw new MusicInfoException(ex);
        }
        logger.debug("ConnectionCreator is initialized.");
    }

    /*
     * Creates tables
     */
    private void createTable() throws SQLException, IOException {
        Properties dbTablesProp = initilizePropMySql(createTableFileName);
        String[] tables = {"mysql.create_table.genre", "mysql.create_table.author", "mysql.create_table.musicinfo"};
        for (String tableCreate : tables) {
            String sqlCreate = dbTablesProp.getProperty(tableCreate);
            try (Statement stat = conn.createStatement()) {
                stat.executeUpdate(sqlCreate);
                logger.debug("Table: {} is created",
                        sqlCreate.substring(sqlCreate.indexOf("TABLE "), sqlCreate.indexOf("(")));
            } catch (SQLException ex) {
                    /*
                     * SQLSTATE: X0Y32 is <value> '<value>' already exists in
					 * <value> '<value>'. if it wasn't the reason of
					 * SQLException then the Exception will be thrown again
					 */
                if (ex.getSQLState().equals("X0Y32"))
                    logger.debug("Table exists");
                else
                    throw ex;
            }
        }

    }

    protected void initilizePrepStatements(String sqlEmplPropertiesFileName, String... sqlPrepStat) throws MusicInfoException {
        try {
            Properties dbProperties = new Properties();
            dbProperties.load(getClass().getResourceAsStream(sqlEmplPropertiesFileName));

            String sql = null;
            prepStatements = new PreparedStatement[dbProperties.size()];
            for (int i = 0; i < sqlPrepStat.length; i++) {
                sql = dbProperties.getProperty(sqlPrepStat[i]);
                prepSt = conn.prepareStatement(sql);
                prepStatements[i] = prepSt;
            }
            logger.debug("{} is fully initialised", this.getClass().getName());
        } catch (IOException | SQLException ex) {
            throw new MusicInfoException(ex);
        }
    }

    public void close() throws SQLException {
        if (conn != null) {
            /*
             * skiping unfinished TRANSACTIONS
			 */
            conn.rollback();
            conn.close();
        }
        logger.debug("DB Connection is closed.");
    }

}