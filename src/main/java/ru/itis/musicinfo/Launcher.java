package ru.itis.musicinfo;

import ru.itis.musicinfo.loader.PageIterator;
import ru.itis.musicinfo.utils.PropertiesSingelton;
import ru.itis.musicinfo.utils.TruncateTables;

import java.io.IOException;
import java.util.Properties;

/**
 * Start programm
 */
public class Launcher {
    public static void main(String[] args) throws Exception {
//        deleteAll();
//        deleteAllOfGenre(PropertiesSingelton.getInstance().getProperty("genre_8"));
        execute();
    }

    public static void execute() throws IOException {
        String genreName = PropertiesSingelton.getInstance().getProperty("genre_12");//ToDo iterate by genres
        PageIterator pageIterator = new PageIterator();
        pageIterator.proceed(genreName);
    }

    public static void deleteAll() {
        TruncateTables tr = new TruncateTables();
        tr.truncateTables();
    }

    public static void deleteAllOfGenre(String genreName) {
        TruncateTables tr = new TruncateTables();
        tr.truncateTablesOfGenre(genreName);
    }
}
