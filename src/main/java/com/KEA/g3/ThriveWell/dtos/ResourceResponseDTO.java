package com.KEA.g3.ThriveWell.dtos;

import java.util.ArrayList;
import java.util.List;

public class ResourceResponseDTO {
    private List<YoutubeVideoDTO> videos = new ArrayList<>();
    private List<NewsArticleDTO> articles = new ArrayList<>();

    public List<YoutubeVideoDTO> getVideos() {
        return videos;
    }

    public void setVideos(List<YoutubeVideoDTO> videos) {
        this.videos = videos;
    }

    public List<NewsArticleDTO> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsArticleDTO> articles) {
        this.articles = articles;
    }
}