package hu.progmasters.dailybugle.service;


import hu.progmasters.dailybugle.domain.*;
import hu.progmasters.dailybugle.dto.incoming.ArticleCommand;
import hu.progmasters.dailybugle.dto.outgoing.*;
import hu.progmasters.dailybugle.exception.AccessDeniedException;
import hu.progmasters.dailybugle.exception.ArticleNotFoundException;
import hu.progmasters.dailybugle.repository.ArticleRepository;
import hu.progmasters.dailybugle.repository.KeywordRepository;
import hu.progmasters.dailybugle.security.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class ArticleService {


    private final ArticleRepository articleRepository;

    private final ModelMapper modelMapper;

    private final CurrentUserProvider currentUserProvider;

    private final KeywordRepository keywordRepository;


    public ArticleService(ArticleRepository articleRepository, ModelMapper modelMapper, CurrentUserProvider currentUserProvider, KeywordRepository keywordRepository) {
        this.articleRepository = articleRepository;
        this.modelMapper = modelMapper;
        this.currentUserProvider = currentUserProvider;
        this.keywordRepository = keywordRepository;
    }

    public void createArticle(ArticleCommand articleCommand) {

        Article article = modelMapper.map(articleCommand, Article.class);
        article.setAuthor(currentUserProvider.getCurrentUser());
        article.setStatus(Status.ACTIVE);

        List<String> keywordNames = articleCommand.getKeywords();

        if (keywordNames != null && !keywordNames.isEmpty()) {

            Set<String> normalizedSet = keywordNames.stream()
                    .map(name -> name.trim().toLowerCase())
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.toSet());

            if (!normalizedSet.isEmpty()) {

                List<Keyword> existingKeywords =
                        keywordRepository.findAllByNameIn(new ArrayList<>(normalizedSet));

                Set<String> existingNames = existingKeywords.stream()
                        .map(Keyword::getName)
                        .collect(Collectors.toSet());

                List<Keyword> newKeywords = normalizedSet.stream()
                        .filter(name -> !existingNames.contains(name))
                        .map(name -> {
                            Keyword keyword = new Keyword();
                            keyword.setName(name);
                            return keyword;
                        })
                        .collect(Collectors.toList());

                if (!newKeywords.isEmpty()) {
                    keywordRepository.saveAll(newKeywords);
                }

                Set<Keyword> attachedKeywords = new HashSet<>();
                attachedKeywords.addAll(existingKeywords);
                attachedKeywords.addAll(newKeywords);

                article.setKeywords(attachedKeywords);
            }
        }

        articleRepository.save(article);
    }


    public List<ArticlesListItem> getAllArticles() {
        return articleRepository.findByStatus(Status.ACTIVE)
                .stream()
                .map(this::mapToListItem)
                .toList();
    }


    public ArticleDetail getArticleById(Long id) {
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

        result.setKeywords(
                article.getKeywords().stream()
                        .map(Keyword::getName)
                        .toList()
        );

        return result;
    }

    public Article findArticleById(Long id) {
        return articleRepository.findByIdAndStatus(id, Status.ACTIVE)
                .orElseThrow(() -> new ArticleNotFoundException("No article found with id: " + id));
    }

    public List<ArticlesListItem> getArticlesByAuthor(Long authorId) {

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
        result.setCategory(article.getCategory());

        result.setKeywords(
                article.getKeywords().stream()
                        .map(Keyword::getName)
                        .toList()
        );

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
        article.setCategory(articleCommand.getCategory());

        List<String> keywordNames = articleCommand.getKeywords();

        if (keywordNames != null) {

            Set<String> normalizedSet = keywordNames.stream()
                    .map(name -> name.trim().toLowerCase())
                    .filter(name -> !name.isEmpty())
                    .collect(Collectors.toSet());

            if (!normalizedSet.isEmpty()) {

                List<Keyword> existingKeywords = keywordRepository.findAllByNameIn(new ArrayList<>(normalizedSet));

                Set<String> existingNames = existingKeywords.stream()
                        .map(Keyword::getName)
                        .collect(Collectors.toSet());

                List<Keyword> newKeywords = normalizedSet.stream()
                        .filter(name -> !existingNames.contains(name))
                        .map(name -> {
                            Keyword keyword = new Keyword();
                            keyword.setName(name);
                            return keyword;
                        })
                        .collect(Collectors.toList());

                if (!newKeywords.isEmpty()) {
                    keywordRepository.saveAll(newKeywords);
                }

                Set<Keyword> attachedKeywords = new HashSet<>();
                attachedKeywords.addAll(existingKeywords);
                attachedKeywords.addAll(newKeywords);

                article.setKeywords(attachedKeywords);

            } else {
                article.getKeywords().clear();
            }
        }

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


    public List<ArticlesListItem> getArticlesByCategory(Category category) {

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);
        return articleRepository.findPublicByCategory(Status.ACTIVE, now, category, pageable)
                .stream()
                .map(this::mapToListItem)
                .toList();

    }

    public List<ArticlesListItem> getArticlesByKeyword(String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new IllegalArgumentException("Keyword must not be empty");
        }

        String normalized = keyword.trim().toLowerCase();

        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 10);

        return articleRepository.findPublicByKeyword(
                        Status.ACTIVE,
                        now,
                        normalized,
                        pageable)
                .stream()
                .map(this::mapToListItem)
                .toList();
    }


    public ArticleFormInitData getMovieFormInitData() {
        List<CategoryDetails> categoryDetailsList = new ArrayList<>();
        for (Category value : Category.values()) {
            CategoryDetails categoryDetails = new CategoryDetails(value.name(), value.getDisplayName());
            categoryDetailsList.add(categoryDetails);
        }
        return new ArticleFormInitData(categoryDetailsList);
    }
}
