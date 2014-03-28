package ru.itis.musicinfo.dao;

import ru.itis.musicinfo.model.MusicInfo;

/**
 * Available operations with music's information
 */
public interface MusicInfoDAO extends AutoCloseable{
    /**
     * such params as name, text and genre must be setted (they can not be null)
     * @param musicInfo
     * @return
     */
    boolean create(MusicInfo musicInfo);
    int getTotalCount();
}
