package com.portfolio.github;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GitHubUser {
  @JsonProperty("avatar_url")
  private String avatarUrl;

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }
}
