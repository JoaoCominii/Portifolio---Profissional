package com.portfolio.github;

public record FeaturedRepo(
  long id,
  String name,
  String description,
  String imageUrl,
  String htmlUrl,
  String homepage,
  String language,
  int stars,
  String updatedAt
) {
  public static FeaturedRepo from(GitHubRepo repo) {
    return new FeaturedRepo(
      repo.getId(),
      repo.getName(),
      repo.getDescription(),
      buildImageUrl(repo),
      repo.getHtmlUrl(),
      repo.getHomepage(),
      repo.getLanguage(),
      repo.getStargazersCount(),
      repo.getUpdatedAt()
    );
  }

  private static String buildImageUrl(GitHubRepo repo) {
    String fullName = repo.getFullName();
    if (fullName == null || fullName.isBlank()) {
      return "";
    }
    return "https://opengraph.githubassets.com/1/" + fullName;
  }
}
