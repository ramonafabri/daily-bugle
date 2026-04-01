package hu.progmasters.dailybugle.service;


import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Rating;
import hu.progmasters.dailybugle.domain.User;
import hu.progmasters.dailybugle.dto.incoming.RatingCommand;
import hu.progmasters.dailybugle.exception.AlreadyRatedException;
import hu.progmasters.dailybugle.repository.RatingRepository;
import hu.progmasters.dailybugle.security.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class RatingService {

    private final RatingRepository ratingRepository;
    private  final ArticleService articleService;
    private final CurrentUserProvider currentUserProvider;
    private final ModelMapper modelMapper;

    public RatingService(RatingRepository ratingRepository, ArticleService articleService, CurrentUserProvider currentUserProvider, ModelMapper modelMapper) {
        this.ratingRepository = ratingRepository;
        this.articleService = articleService;
        this.currentUserProvider = currentUserProvider;
        this.modelMapper = modelMapper;
    }


    public void createRating(Long articleId, RatingCommand ratingCommand) {

        log.info("RATING VALUE: {}", ratingCommand.getValue());

        User currentUser = currentUserProvider.getCurrentUser();
        Article article = articleService.findArticleById(articleId);

        boolean alreadyRated = ratingRepository.existsByArticle_IdAndUser_Id(
                        article.getId(),
                        currentUser.getId());

        if (alreadyRated) {
            throw new AlreadyRatedException("User already rated this article");
        }

        Rating rating = new Rating();
        rating.setValue(ratingCommand.getValue());
        rating.setArticle(article);
        rating.setUser(currentUser);

        ratingRepository.save(rating);

        log.info("User {} rated article {} with {}",
                currentUser.getId(),
                article.getId(),
                ratingCommand.getValue());
    }


}
