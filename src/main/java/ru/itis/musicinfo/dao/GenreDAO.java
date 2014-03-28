package ru.itis.musicinfo.dao;

import ru.itis.musicinfo.model.Genre;
import ru.itis.musicinfo.model.MusicInfo;

import java.util.List;

/**
 * Available operations on Genre
 */
public interface GenreDAO extends AutoCloseable{
    Long create(String name);
    int getMusicCount(Genre genre);
    List<MusicInfo> getMusicList(Genre genre);
    Genre get(String name);
}
