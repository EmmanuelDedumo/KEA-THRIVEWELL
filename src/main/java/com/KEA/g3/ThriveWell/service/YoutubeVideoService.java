package com.KEA.g3.ThriveWell.service;

import com.KEA.g3.ThriveWell.dtos.YoutubeApiResponse;
import com.KEA.g3.ThriveWell.dtos.YoutubeVideoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class YoutubeVideoService {

    @Value("${youtube.api.key:AIzaSyBaucTSp_sRxuQuA2JArTdLhFUeay4Lyww}")
    private String YOUTUBE_API_KEY;

    private static final String YOUTUBE_API_URL = "https://www.googleapis.com/youtube/v3/search";

    public List<YoutubeVideoDTO> fetchVideos(String query, int maxResults) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);

        // Build URL with query parameters
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(YOUTUBE_API_URL)
                .queryParam("part", "snippet")
                .queryParam("maxResults", maxResults)
                .queryParam("q", query + " mental health mood")
                .queryParam("type", "video")
                .queryParam("key", YOUTUBE_API_KEY);

        String fullUrl = builder.toUriString();
        System.out.println("Making request to YouTube API: " + fullUrl.replace(YOUTUBE_API_KEY, "API_KEY_HIDDEN"));

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<YoutubeApiResponse> response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    entity,
                    YoutubeApiResponse.class);

            System.out.println("YouTube API Response Status: " + response.getStatusCode());

            if (response.getBody() == null || response.getBody().getItems() == null) {
                System.out.println("YouTube API returned null body or items list");
                return new ArrayList<>();
            }

            List<YoutubeVideoDTO> videos = new ArrayList<>();
            System.out.println("Found " + response.getBody().getItems().size() + " videos");

            for (YoutubeApiResponse.Item item : response.getBody().getItems()) {
                YoutubeVideoDTO video = new YoutubeVideoDTO();

                // Check if the id and videoId are not null
                if (item.getId() != null && item.getId().getVideoId() != null) {
                    video.setVideoId(item.getId().getVideoId());
                } else {
                    // Skip this item if it doesn't have a valid video ID
                    System.out.println("Skipping item with null ID");
                    continue;
                }

                if (item.getSnippet() != null) {
                    video.setTitle(item.getSnippet().getTitle());
                    video.setDescription(item.getSnippet().getDescription());
                    video.setPublishedAt(item.getSnippet().getPublishedAt());
                    video.setChannelTitle(item.getSnippet().getChannelTitle());

                    // Get thumbnail URL if available
                    if (item.getSnippet().getThumbnails() != null &&
                            item.getSnippet().getThumbnails().getMedium() != null) {
                        video.setThumbnailUrl(item.getSnippet().getThumbnails().getMedium().getUrl());
                    } else if (item.getSnippet().getThumbnails() != null &&
                            item.getSnippet().getThumbnails().getDefault() != null) {
                        video.setThumbnailUrl(item.getSnippet().getThumbnails().getDefault().getUrl());
                    }
                }

                videos.add(video);
            }

            return videos;
        } catch (Exception e) {
            System.err.println("Error fetching YouTube videos: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}