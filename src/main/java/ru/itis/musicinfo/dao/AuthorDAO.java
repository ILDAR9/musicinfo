package ru.itis.musicinfo.dao;

import ru.itis.musicinfo.model.Author;
import ru.itis.musicinfo.model.MusicInfo;

import java.util.List;

/**
 * Available operations on AuthorDAO
 */
public interface AuthorDAO extends AutoCloseable {
    Long create(String name);
    Author get(String name);
    int getSoundCount(Author author);
    List<MusicInfo> getMusicList(Author author);
}
