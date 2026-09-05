package com.portfolio.api;

import com.portfolio.config.ProfileProperties;
import com.portfolio.model.Project;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = { "http://localhost:5173" })
public class ProfileController {
  private final ProfileProperties profile;

  public ProfileController(ProfileProperties profile) {
    this.profile = profile;
  }

  @GetMapping("/profile")
  public ProfileResponse profile() {
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

    return new ProfileResponse(
      profile.getName(),
      profile.getTitle(),
      profile.getBlurb(),
      profile.getPhotoUrl(),
      profile.getAbout(),
      profile.getLocation(),
      profile.getGithub(),
      links,
      stack,
      languages,
      profile.getFeaturedProjects()
    );
  }

  @GetMapping("/projects")
  public List<Project> projects() {
    return profile.getFeaturedProjects().stream()
      .sorted((a, b) -> Integer.compare(a.getOrder(), b.getOrder()))
      .collect(Collectors.toList());
  }

  @GetMapping("/project/{id}")
  public Project project(@PathVariable("id") Long id) {
    if (id == null) {
      return null;
    }
    return profile.getFeaturedProjects().stream()
      .filter(p -> p.getId() == id)
      .findFirst()
      .orElse(null);
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
    List<LanguageResponse> languages,
    List<Project> featuredProjects
  ) {}

  public record LinkResponse(String label, String url) {}
  public record StackResponse(List<String> mastered, List<String> learning) {}
  public record LanguageResponse(String name, String level) {}
}