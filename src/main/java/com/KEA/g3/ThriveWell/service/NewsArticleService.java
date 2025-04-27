package com.KEA.g3.ThriveWell.service;

import com.KEA.g3.ThriveWell.dtos.NewsApiResponse;
import com.KEA.g3.ThriveWell.dtos.NewsArticleDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;

@Service
public class NewsArticleService {

    @Value("${newsapi.api.key:7388ee693de140359871e34a70d78025}")
    private String NEWS_API_KEY;

    private static final String NEWS_API_URL = "https://newsapi.org/v2/everything";

    public List<NewsArticleDTO> fetchArticles(String query, int maxResults) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.set("X-Api-Key", NEWS_API_KEY);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(NEWS_API_URL)
                .queryParam("q", query)
                .queryParam("language", "en")
                .queryParam("sortBy", "relevancy")
                .queryParam("pageSize", maxResults);

        String fullUrl = builder.toUriString();
        System.out.println("Making request to News API: " + fullUrl);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<NewsApiResponse> response = restTemplate.exchange(
                    fullUrl,
                    HttpMethod.GET,
                    entity,
                    NewsApiResponse.class);

            System.out.println("News API Response Status: " + response.getStatusCode());

            if (response.getBody() == null || response.getBody().getArticles() == null) {
                System.out.println("News API returned null body or articles list");
                return new ArrayList<>();
            }

            List<NewsArticleDTO> articles = new ArrayList<>();
            for (NewsApiResponse.Article item : response.getBody().getArticles()) {
                NewsArticleDTO dto = new NewsArticleDTO();
                dto.setTitle(item.getTitle());
                dto.setDescription(item.getDescription());
                dto.setUrl(item.getUrl());
                dto.setUrlToImage(item.getUrlToImage());
                dto.setPublishedAt(item.getPublishedAt());
                dto.setSource(item.getSource() != null ? item.getSource().getName() : "Unknown");
                articles.add(dto);
            }

            System.out.println("Mapped " + articles.size() + " articles");
            return articles;

        } catch (Exception e) {
            System.err.println("Error fetching articles: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
