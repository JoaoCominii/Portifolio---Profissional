package com.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
  @Bean
  public WebClient githubWebClient(GitHubProperties properties) {
    return WebClient.builder()
      .baseUrl("https://api.github.com")
      .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
      .defaultHeader(HttpHeaders.USER_AGENT, "devportfolio-app")
      .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
      .filter((request, next) -> {
        if (properties.getToken() != null && !properties.getToken().isBlank()) {
          request = org.springframework.web.reactive.function.client.ClientRequest.from(request)
              .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getToken())
              .build();
        }
        return next.exchange(request);
      })
      .build();
  }
}
