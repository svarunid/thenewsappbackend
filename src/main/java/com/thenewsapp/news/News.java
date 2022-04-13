package com.thenewsapp.news;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thenewsapp.user.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "news")
@Getter
@Setter
public class News {
    @Column
    private String source;

    @Column
    private String author;

    @Column
    private String title;

    @Column(length = 5000)
    private String description;

    @Id
    private String url;

    @Column
    private String urlToImage;

    @Column
    private String publishedAt;

    @ManyToMany
    @JoinTable(
            name = "saved_news",
            joinColumns = @JoinColumn(name = "url"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    @JsonIgnore
    Set<User> users;
}