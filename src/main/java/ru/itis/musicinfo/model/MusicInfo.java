package ru.itis.musicinfo.model;

/**
 * <ol><li>name</li>
 * <li>author: Foreign key to Author</li>
 * <li>genre: Foreign key to Genre</li>
 * <li>text</li></ol>
 */
public class MusicInfo {
    private Long id;
    private String name;
    private Long author_id;
    private String text;
    private Long genre_id;


    public MusicInfo() {
    }

    public MusicInfo(String name, Long genre_id) {
        this.name = name;
        this.genre_id = genre_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAuthor_id() {
        return author_id;
    }

    public void setAuthor_id(Long author_id) {
        this.author_id = author_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getGenre_id() {
        return genre_id;
    }

    public void setGenre_id(Long genre_id) {
        this.genre_id = genre_id;
    }


    @Override
    public String toString() {
        return "MusicInfo{" +
                "name='" + name + '\'' +
                ", author_id=" + author_id +
                ", genre_id=" + genre_id +
                '}';
    }
}
