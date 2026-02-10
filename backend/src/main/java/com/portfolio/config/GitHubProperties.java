package com.portfolio.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.github")
public class GitHubProperties {
  private String username;
  private String token;
  private int perPage = 6;
  private boolean excludeForks = true;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getPerPage() {
    return perPage;
  }

  public void setPerPage(int perPage) {
    this.perPage = perPage;
  }

  public boolean isExcludeForks() {
    return excludeForks;
  }

  public void setExcludeForks(boolean excludeForks) {
    this.excludeForks = excludeForks;
  }
}
