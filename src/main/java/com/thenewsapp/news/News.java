package com.thenewsapp.news;

import lombok.Data;

@Data
class News {
    private String source;
    private String author;
    private String title;
    private String description;
    private String url;
    private String urlToImage;
    private String publishedAt;
}