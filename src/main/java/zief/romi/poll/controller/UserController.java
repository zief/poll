package zief.romi.poll.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import zief.romi.poll.exception.ResourceNotFoundException;
import zief.romi.poll.model.User;
import zief.romi.poll.payload.PagedResponse;
import zief.romi.poll.payload.PollResponse;
import zief.romi.poll.payload.UserIdentityAvailability;
import zief.romi.poll.payload.UserProfile;
import zief.romi.poll.payload.UserSummary;
import zief.romi.poll.repository.PollRepository;
import zief.romi.poll.repository.UserRepository;
import zief.romi.poll.repository.VoteRepository;
import zief.romi.poll.security.CurrentUser;
import zief.romi.poll.security.UserPrincipal;
import zief.romi.poll.service.PollService;
import zief.romi.poll.util.AppConstants;

@RestController
@RequestMapping("/api")
public class UserController {
  @Autowired private UserRepository userRepository;

  @Autowired private PollRepository pollRepository;

  @Autowired private VoteRepository voteRepository;

  @Autowired private PollService pollService;

  private static final Logger logger = LoggerFactory.getLogger(UserController.class);

  @GetMapping("/user/me")
  @PreAuthorize("hasRole('USER')")
  public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser) {
    UserSummary userSummary =
        new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getName());
    return userSummary;
  }

  @GetMapping("/user/checkUsernameAvailability")
  public UserIdentityAvailability checkUsernameAvailability(
      @RequestParam(value = "username") String username) {
    Boolean isAvailable = !userRepository.existsByUsername(username);
    return new UserIdentityAvailability(isAvailable);
  }

  @GetMapping("/user/checkEmailAvailability")
  public UserIdentityAvailability checkEmailAvailability(
      @RequestParam(value = "email") String email) {
    Boolean isAvailable = !userRepository.existsByEmail(email);
    return new UserIdentityAvailability(isAvailable);
  }

  @GetMapping("/users/{username}")
  public UserProfile getUserProfile(@PathVariable(value = "username") String username) {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

    long pollCount = pollRepository.countByCreatedBy(user.getId());
    long voteCount = voteRepository.countByUserId(user.getId());

    UserProfile userProfile =
        new UserProfile(
            user.getId(),
            user.getUsername(),
            user.getName(),
            user.getCreatedAt(),
            pollCount,
            voteCount);

    return userProfile;
  }

  @GetMapping("/users/{username}/polls")
  public PagedResponse<PollResponse> getPollsCreatedBy(
      @PathVariable(value = "username") String username,
      @CurrentUser UserPrincipal currentUser,
      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
    return pollService.getPollsCreatedBy(username, currentUser, page, size);
  }

  @GetMapping("/users/{username}/votes")
  public PagedResponse<PollResponse> getPollsVotedBy(
      @PathVariable(value = "username") String username,
      @CurrentUser UserPrincipal currentUser,
      @RequestParam(value = "page", defaultValue = AppConstants.DEFAULT_PAGE_NUMBER) int page,
      @RequestParam(value = "size", defaultValue = AppConstants.DEFAULT_PAGE_SIZE) int size) {
    return pollService.getPollsVotedBy(username, currentUser, page, size);
  }
}
