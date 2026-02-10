package com.portfolio.api;

import com.portfolio.config.ProfileProperties;
import com.portfolio.github.FeaturedRepo;
import com.portfolio.github.GitHubService;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173" })
public class ProfileController {
  private final ProfileProperties profile;
  private final GitHubService gitHubService;

  public ProfileController(ProfileProperties profile, GitHubService gitHubService) {
    this.profile = profile;
    this.gitHubService = gitHubService;
  }

  @GetMapping("/profile")
  public Mono<ProfileResponse> profile() {
    List<LinkResponse> links = profile.getLinks().stream()
      .map(link -> new LinkResponse(link.getLabel(), link.getUrl()))
      .collect(Collectors.toList());

    List<LanguageResponse> languages = profile.getLanguages() != null 
      ? profile.getLanguages().stream()
          .map(lang -> new LanguageResponse(lang.getName(), lang.getLevel()))
          .collect(Collectors.toList())
      : List.of();

    StackResponse stack = new StackResponse(
      profile.getStack().getMastered(),
      profile.getStack().getLearning()
    );

    String configuredPhoto = profile.getPhotoUrl();
    Mono<String> photoMono = (configuredPhoto != null && !configuredPhoto.isBlank())
      ? Mono.just(configuredPhoto)
      : gitHubService.getAvatarUrl();

    return photoMono.map(photoUrl -> new ProfileResponse(
      profile.getName(),
      profile.getTitle(),
      profile.getBlurb(),
      photoUrl,
      profile.getAbout(),
      profile.getLocation(),
      profile.getGithub(),
      links,
      stack,
      languages
    ));
  }

  @GetMapping("/repos")
  public Mono<List<FeaturedRepo>> repos() {
    return gitHubService.getFeaturedRepos();
  }

  public record ProfileResponse(
    String name,
    String title,
    String blurb,
    String photoUrl,
    String about,
    String location,
    String github,
    List<LinkResponse> links,
    StackResponse stack,
    List<LanguageResponse> languages
  ) {}

  public record LinkResponse(String label, String url) {}

  public record StackResponse(List<String> mastered, List<String> learning) {}

  public record LanguageResponse(String name, String level) {}
}
