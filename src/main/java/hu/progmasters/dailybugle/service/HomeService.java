package hu.progmasters.dailybugle.service;

import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Rating;
import hu.progmasters.dailybugle.domain.Status;
import hu.progmasters.dailybugle.dto.outgoing.ArticlesListItem;
import hu.progmasters.dailybugle.dto.outgoing.HomePageResponse;
import hu.progmasters.dailybugle.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@Slf4j
public class HomeService {

    private final ArticleRepository articleRepository;
    private final ModelMapper modelMapper;


    public HomeService(ArticleRepository articleRepository, ModelMapper modelMapper) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
    }

    public HomePageResponse getHomePage() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fromDate = now.minusDays(3);
        Pageable topTen = PageRequest.of(0, 10);

        List<Article> listOfLatestPublic =  articleRepository.findLatestPublic(
                Status.ACTIVE,
                now,
                topTen
        );


       List<Article> listOfTopRatedPublic =  articleRepository.findTopRatedPublic(
               Status.ACTIVE,
                now,
                topTen
       );

       List<Article> listOfTopRatedLast3Days = articleRepository.findTopRatedPublicLast3Days(
               Status.ACTIVE,
               now,
               fromDate,
               topTen
       );

       List<ArticlesListItem> latestArticles = listOfLatestPublic.stream()
               .map(this::mapToListItem)
               .toList();

       List<ArticlesListItem> topRatedArticles = listOfTopRatedPublic.stream()
               .map(this::mapToListItem)
               .toList();

        List<ArticlesListItem> topRatedLast3DaysArticles = listOfTopRatedLast3Days.stream()
                .map(this::mapToListItem)
                .toList();

        HomePageResponse response = new HomePageResponse();
        response.setLatest(latestArticles);
        response.setTopRated(topRatedArticles);
        response.setTopRatedLast3Days(topRatedLast3DaysArticles);

        return response;

    }

    private ArticlesListItem mapToListItem(Article article) {

        ArticlesListItem articlesListItem = new ArticlesListItem();

        articlesListItem.setId(article.getId());
        articlesListItem.setTitle(article.getTitle());
        articlesListItem.setSynopsis(article.getSynopsis());
        articlesListItem.setAuthor(article.getAuthor().getDisplayName());

        articlesListItem.setCommentCount((long) article.getComments().size());

        long ratingCount = article.getRatings().size();
        articlesListItem.setRatingCount(ratingCount);

        if (ratingCount == 0) {
            articlesListItem.setAverageRating(BigDecimal.ZERO.setScale(2));
        } else {
            double avg = article.getRatings().stream()
                    .mapToInt(Rating::getValue)
                    .average()
                    .orElse(0.0);

            articlesListItem.setAverageRating(
                    BigDecimal.valueOf(avg).setScale(2, RoundingMode.HALF_UP)
            );
        }

        return articlesListItem;
    }





}
