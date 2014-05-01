package ru.itis.musicinfo.utils;

import java.util.List;

/**
 * Utils for manipulation with data
 */
public class DataUtils {
    public static boolean isEmpty(String item){
        return item == null || item.trim().isEmpty();
    }
    public static boolean isEmpty(List items){
        return items==null || items.size() == 0;
    }
}
