package ru.itis.musicinfo.model;

/**
 * dictionary of genre's types
 */
public class Genre {
    private Long id;
    private String name;

    public Genre(){}
    public Genre(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Genre{" +
                "name='" + name + '\'' +
                '}';
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
