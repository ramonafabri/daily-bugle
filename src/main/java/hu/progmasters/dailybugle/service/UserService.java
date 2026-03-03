package hu.progmasters.dailybugle.service;

import hu.progmasters.dailybugle.domain.*;
import hu.progmasters.dailybugle.dto.incoming.LoginCommand;
import hu.progmasters.dailybugle.dto.incoming.RegisterCommand;
import hu.progmasters.dailybugle.dto.outgoing.*;
import hu.progmasters.dailybugle.exception.*;
import hu.progmasters.dailybugle.repository.ArticleRepository;
import hu.progmasters.dailybugle.repository.CommentRepository;
import hu.progmasters.dailybugle.repository.RatingRepository;
import hu.progmasters.dailybugle.repository.UserRepository;
import hu.progmasters.dailybugle.security.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class UserService {

    private static final String INACTIVE_DISPLAY_NAME = "*inaktív regisztráció*";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final ArticleRepository articleRepository;
    private final CurrentUserProvider currentUserProvider;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RatingRepository ratingRepository, CommentRepository commentRepository, ArticleRepository articleRepository, CurrentUserProvider currentUserProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.ratingRepository = ratingRepository;
        this.commentRepository = commentRepository;
        this.articleRepository = articleRepository;
        this.currentUserProvider = currentUserProvider;
    }

    public void register(RegisterCommand registerCommand) {

        String normalizedEmail = registerCommand.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException("Email already exists: " + registerCommand.getEmail());
        }

        if (registerCommand.getRole() == Role.ADMIN) {
            throw new IllegalArgumentException("Cannot register as ADMIN");
        }

        User user = new User();
        user.setEmail(normalizedEmail);
        user.setDisplayName(registerCommand.getDisplayName());
        user.setRole(registerCommand.getRole());

        String hashedPassword = passwordEncoder.encode(registerCommand.getPassword());
        user.setPasswordHash(hashedPassword);
        log.info("User registered: {}", user);

        userRepository.save(user);
    }


    public LoginResponse login(LoginCommand loginCommand) {

        String normalizedEmail = loginCommand.getEmail().trim().toLowerCase();

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));

        if (user.getStatus() != Status.ACTIVE) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        boolean passwordMatches =
                passwordEncoder.matches(loginCommand.getPassword(), user.getPasswordHash());

        if (!passwordMatches) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return new LoginResponse(
                user.getId(),
                user.getDisplayName(),
                user.getRole());
    }

    public UserProfileResponse getUserProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        UserProfileResponse response = new UserProfileResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setDisplayName(user.getDisplayName());
        response.setRole(user.getRole());


        List<Rating> ratings = ratingRepository.findByUser_Id(user.getId());

        List<UserRatingInfo> ratingInfos = ratings.stream()
                .map(rating -> new UserRatingInfo(
                        rating.getArticle().getId(),
                        rating.getArticle().getTitle(),
                        rating.getValue()
                ))
                .toList();

        response.setRatings(ratingInfos);


        List<Comment> comments = commentRepository.findByUser_Id(user.getId());

        Map<Article, Long> grouped = comments.stream()
                        .collect(Collectors.groupingBy(
                                Comment::getArticle,
                                Collectors.counting()));

        List<UserCommentInfo> commentInfos = grouped.entrySet().stream()
                        .map(entry -> new UserCommentInfo(
                                entry.getKey().getId(),
                                entry.getKey().getTitle(),
                                entry.getValue()
                        ))
                        .toList();

        response.setComments(commentInfos);


        if (user.getRole() == Role.JOURNALIST) {

            LocalDateTime now = LocalDateTime.now();

            List<Article> writtenArticles =
                    articleRepository.findPublicByAuthor(
                            user.getId(),
                            Status.ACTIVE,
                            now
                    );

            List<UserArticleInfo> articleInfos =
                    writtenArticles.stream()
                            .map(article -> new UserArticleInfo(
                                    article.getId(),
                                    article.getTitle()
                            ))
                            .toList();

            response.setWrittenArticles(articleInfos);
        }

        return response;

    }

    public void deactivateUser(Long userId) {

        User currentUser = currentUserProvider.getCurrentUser();

        if (currentUser.getRole() != Role.ADMIN) {
            throw new AccessDeniedException("Only admin can deactivate users");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));

        if (user.getStatus() == Status.INACTIVE) {
            log.info("User {} is already inactive", userId);
            return;
        }

        if (currentUser.getId().equals(userId)) {
            throw new InvalidAdminOperationException("Admin cannot deactivate himself");
        }

        user.setStatus(Status.INACTIVE);
        user.setEmail("deleted_user_" + user.getId() + "@deleted.local");
        user.setDisplayName(INACTIVE_DISPLAY_NAME);
        user.setPasswordHash("DEACTIVATED_" + user.getId());

        log.info("User {} has been deactivated by admin {}", userId, currentUser.getId());
    }



}
