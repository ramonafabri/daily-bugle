package hu.progmasters.dailybugle.service;


import hu.progmasters.dailybugle.domain.*;
import hu.progmasters.dailybugle.dto.incoming.ArticleCommand;
import hu.progmasters.dailybugle.dto.outgoing.ArticleDetail;
import hu.progmasters.dailybugle.dto.outgoing.ArticlesListItem;
import hu.progmasters.dailybugle.dto.outgoing.CommentDetail;
import hu.progmasters.dailybugle.exception.AccessDeniedException;
import hu.progmasters.dailybugle.exception.ArticleNotFoundException;
import hu.progmasters.dailybugle.repository.ArticleRepository;
import hu.progmasters.dailybugle.security.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ArticleService {


    private final ArticleRepository articleRepository;

    private final ModelMapper modelMapper;

    private final CurrentUserProvider currentUserProvider;


    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper, CurrentUserProvider currentUserProvider) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.currentUserProvider = currentUserProvider;
    }

    public void createArticle(ArticleCommand articleCommand) {

        User currentUser = currentUserProvider.getCurrentUser();

        if (currentUser.getRole() != Role.JOURNALIST) {
            throw new AccessDeniedException("Only journalists can create articles");
        }

        Article article = modelMapper.map(articleCommand, Article.class);
        article.setAuthor(currentUser);

        articleRepository.save(article);

        log.info("Article created by user: {}", currentUser.getId());
    }


    public List<ArticlesListItem> getAllArticles() {
        return articleRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(this::mapToListItem)
                .toList();
    }


    public ArticleDetail getArticleById(Long id){
        LocalDateTime now = LocalDateTime.now();

        Article article = articleRepository
                .findPublicById(id, Status.ACTIVE, now)
                .orElseThrow(() ->
                        new ArticleNotFoundException("No public article found with id: " + id));

        ArticleDetail result =
                modelMapper.map(article, ArticleDetail.class);

        result.setAuthor(article.getAuthor().getDisplayName());

        List<CommentDetail> comments = article.getComments().stream()
                .map(comment -> {
                    CommentDetail cd = modelMapper.map(comment, CommentDetail.class);
                    cd.setAuthor(comment.getUser().getDisplayName());
                    return cd;
                })
                .toList();

        result.setComments(comments);

        long ratingCount = article.getRatings().size();
        result.setRatingCount(ratingCount);
        result.setAverageRating(calculateAverageRating(article));

        return result;
    }

    public Article findArticleById(Long id) {
        return articleRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new ArticleNotFoundException("No article found with id: " + id));
    }

    public  List<ArticlesListItem> getArticlesByAuthor(Long authorId){

        LocalDateTime now = LocalDateTime.now();

        return articleRepository
                .findPublicByAuthor(authorId, Status.ACTIVE, now)
                .stream()
                .map(this::mapToListItem)
                .toList();

    }



    private ArticlesListItem mapToListItem(Article article) {

        ArticlesListItem result = modelMapper.map(article, ArticlesListItem.class);

        result.setAuthor(article.getAuthor().getDisplayName());

        result.setCommentCount((long) article.getComments().size());

        long ratingCount = article.getRatings().size();
        result.setRatingCount(ratingCount);
        result.setAverageRating(calculateAverageRating(article));

        return result;
    }


    public void updateArticleById(Long id, ArticleCommand articleCommand) {
        Article article = findArticleById(id);

        article.setTitle(articleCommand.getTitle());
        article.setSynopsis(articleCommand.getSynopsis());
        article.setContent(articleCommand.getContent());
        article.setPublishAt(articleCommand.getPublishAt());

        log.info("Article updated: {}", article.getId());
    }


    public void deleteArticleById(Long id) {
        Article article = findArticleById(id);
        article.setStatus(Status.DELETED);
        log.info("Article with id {} deleted", id);
    }


    private BigDecimal calculateAverageRating(Article article) {

        List<Rating> ratings = article.getRatings();

        if (ratings.isEmpty()) {
            return BigDecimal.ZERO.setScale(2);
        }

        double avg = ratings.stream()
                .mapToInt(Rating::getValue)
                .average()
                .orElse(0.0);

        return BigDecimal.valueOf(avg)
                .setScale(2, RoundingMode.HALF_UP);
    }



}
