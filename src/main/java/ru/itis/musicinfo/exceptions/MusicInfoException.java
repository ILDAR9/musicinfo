package ru.itis.musicinfo.exceptions;

import ru.itis.musicinfo.model.MusicInfo;

public class MusicInfoException extends Exception {
    public MusicInfoException(Exception ex) {
        super(ex);
    }

    public MusicInfoException(String message, Exception ex) {
        super(message, ex);
    }

    public MusicInfoException(String message) {
        super(message);
    }
}
