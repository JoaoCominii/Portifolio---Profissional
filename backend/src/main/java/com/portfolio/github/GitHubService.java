package com.portfolio.github;

import com.portfolio.config.GitHubProperties;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class GitHubService {
  private static final Duration AVATAR_CACHE_TTL = Duration.ofHours(6);
  private final GitHubClient client;
  private final GitHubProperties properties;
  private volatile String cachedAvatarUrl = "";
  private volatile Instant cachedAvatarAt;

  public GitHubService(GitHubClient client, GitHubProperties properties) {
    this.client = client;
    this.properties = properties;
  }

  public Mono<List<FeaturedRepo>> getFeaturedRepos() {
    return client.fetchRepos(properties.getUsername())
      .map(repos -> {
        Stream<GitHubRepo> stream = repos.stream();

        if (properties.isExcludeForks()) {
          stream = stream.filter(repo -> !repo.isFork());
        }

        return stream
          .filter(repo -> Objects.nonNull(repo.getName()))
          .sorted(Comparator
            .comparingInt(GitHubRepo::getStargazersCount)
            .reversed()
            .thenComparing(repo -> parseInstant(repo.getUpdatedAt()),
              Comparator.nullsLast(Comparator.reverseOrder())))
          .limit(properties.getPerPage())
          .map(FeaturedRepo::from)
          .toList();
      })
      .onErrorResume(e -> {
        System.err.println("GitHub API Error (Repos): " + e.getMessage());
        return Mono.just(List.of());
      });
  }

  public Mono<String> getAvatarUrl() {
    String username = properties.getUsername();
    if (username == null || username.isBlank()) {
      return Mono.just("");
    }

    Instant now = Instant.now();
    Instant cachedAt = cachedAvatarAt;
    if (cachedAt != null && now.isBefore(cachedAt.plus(AVATAR_CACHE_TTL))) {
      return Mono.just(cachedAvatarUrl);
    }

    return client.fetchUser(username)
      .map(GitHubUser::getAvatarUrl)
      .defaultIfEmpty("")
      .doOnNext(avatarUrl -> {
        cachedAvatarUrl = avatarUrl;
        cachedAvatarAt = Instant.now();
      })
      .onErrorResume(e -> {
        System.err.println("GitHub API Error (Avatar): " + e.getMessage());
        return Mono.just(""); // Fallback to empty string for fallback UI
      });
  }

  private Instant parseInstant(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    try {
      return Instant.parse(value);
    } catch (DateTimeParseException ex) {
      return null;
    }
  }
}
