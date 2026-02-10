package com.portfolio.github;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class GitHubClient {
  private final WebClient webClient;

  public GitHubClient(WebClient githubWebClient) {
    this.webClient = githubWebClient;
  }

  public Mono<java.util.List<GitHubRepo>> fetchRepos(String username) {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/users/{user}/repos")
        .queryParam("per_page", 100)
        .queryParam("sort", "updated")
        .build(username))
      .retrieve()
      .bodyToMono(new ParameterizedTypeReference<java.util.List<GitHubRepo>>() {})
      .defaultIfEmpty(java.util.List.of());
  }

  public Mono<GitHubUser> fetchUser(String username) {
    return webClient.get()
      .uri(uriBuilder -> uriBuilder
        .path("/users/{user}")
        .build(username))
      .retrieve()
      .bodyToMono(GitHubUser.class);
  }
}
