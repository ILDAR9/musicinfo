package ru.itis.musicinfo.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

public class PropertiesSingelton {
    private static final Logger logger = LoggerFactory.getLogger(PropertiesSingelton.class);

    private static Properties genreProperties;


    private PropertiesSingelton() {

    }

    public static Properties getInstance() {
        if (genreProperties == null) {
            genreProperties = new Properties();
            try {
                genreProperties.load(PropertiesSingelton.class.getResourceAsStream("/ru/itis/musicinfo/loader/genres.properties"));
            } catch (IOException e) {
                logger.error("", e);
            }
        }
        return genreProperties;
    }

}
