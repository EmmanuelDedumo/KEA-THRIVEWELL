package com.KEA.g3.ThriveWell.controller;

import com.KEA.g3.ThriveWell.dtos.NewsArticleDTO;
import com.KEA.g3.ThriveWell.dtos.ResourceResponseDTO;
import com.KEA.g3.ThriveWell.dtos.YoutubeVideoDTO;
import com.KEA.g3.ThriveWell.service.NewsArticleService;
import com.KEA.g3.ThriveWell.service.YoutubeVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/resources")
@CrossOrigin(origins = "*")
public class ResourceController {

    @Autowired
    private YoutubeVideoService youtubeVideoService;

    @Autowired
    private NewsArticleService newsArticleService;

    @GetMapping("/videos")
    public ResponseEntity<List<YoutubeVideoDTO>> getVideos(
            @RequestParam(defaultValue = "mental health") String query,
            @RequestParam(defaultValue = "10") int maxResults) {

        System.out.println("Getting videos with query: " + query + ", maxResults: " + maxResults);
        List<YoutubeVideoDTO> videos = youtubeVideoService.fetchVideos(query, maxResults);
        System.out.println("Found " + videos.size() + " videos");

        return ResponseEntity.ok(videos);
    }

    @GetMapping("/articles")
    public ResponseEntity<List<NewsArticleDTO>> getArticles(
            @RequestParam(defaultValue = "mental health") String query,
            @RequestParam(defaultValue = "10") int maxResults) {

        System.out.println("Getting articles with query: " + query + ", maxResults: " + maxResults);
        List<NewsArticleDTO> articles = newsArticleService.fetchArticles(query, maxResults);
        System.out.println("Found " + articles.size() + " articles");

        return ResponseEntity.ok(articles);
    }

    @GetMapping("/all")
    public ResponseEntity<ResourceResponseDTO> getAllResources(
            @RequestParam(defaultValue = "mental health") String query,
            @RequestParam(defaultValue = "5") int maxResults) {

        System.out.println("Getting all resources with query: " + query + ", maxResults: " + maxResults);

        List<YoutubeVideoDTO> videos = youtubeVideoService.fetchVideos(query, maxResults);
        List<NewsArticleDTO> articles = newsArticleService.fetchArticles(query, maxResults);

        System.out.println("Found " + videos.size() + " videos and " + articles.size() + " articles");

        ResourceResponseDTO response = new ResourceResponseDTO();
        response.setVideos(videos);
        response.setArticles(articles);

        return ResponseEntity.ok(response);
    }

    // Test endpoint that returns hardcoded data for debugging
    @GetMapping("/test")
    public ResponseEntity<ResourceResponseDTO> getTestResources() {
        System.out.println("Getting test resources");

        ResourceResponseDTO resources = new ResourceResponseDTO();

        // Add sample video
        YoutubeVideoDTO video = new YoutubeVideoDTO();
        video.setVideoId("dQw4w9WgXcQ");
        video.setTitle("Test Video Title");
        video.setDescription("This is a test video description");
        video.setThumbnailUrl("https://via.placeholder.com/320x180");
        video.setPublishedAt("2023-01-01T00:00:00Z");
        video.setChannelTitle("Test Channel");
        resources.getVideos().add(video);

        // Add sample article
        NewsArticleDTO article = new NewsArticleDTO();
        article.setTitle("Test Article Title");
        article.setDescription("This is a test article description");
        article.setUrl("https://example.com/article");
        article.setUrlToImage("https://via.placeholder.com/320x180");
        article.setPublishedAt("2023-01-01T00:00:00Z");
        article.setSource("Test Source");
        resources.getArticles().add(article);

        return ResponseEntity.ok(resources);
    }
}