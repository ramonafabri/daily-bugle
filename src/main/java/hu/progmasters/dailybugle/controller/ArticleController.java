package hu.progmasters.dailybugle.controller;


import hu.progmasters.dailybugle.domain.Category;
import hu.progmasters.dailybugle.dto.incoming.ArticleCommand;
import hu.progmasters.dailybugle.dto.outgoing.ArticleDetail;
import hu.progmasters.dailybugle.dto.outgoing.ArticleFormInitData;
import hu.progmasters.dailybugle.dto.outgoing.ArticlesListItem;
import hu.progmasters.dailybugle.service.ArticleService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/articles")
@Slf4j
public class ArticleController {

    private final ArticleService articleService;


    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<Void> createArticle(@Valid @RequestBody ArticleCommand articleCommand) {
        log.info("Create article: {}", articleCommand);
        articleService.createArticle(articleCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @GetMapping
    public ResponseEntity<List<ArticlesListItem>> getAllArticles() {
        log.info("Get all articles");
        List<ArticlesListItem> result = articleService.getAllArticles();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetail> getArticleById(@PathVariable Long id) {
        log.info("Get article by id: {}", id);
        ArticleDetail result = articleService.getArticleById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("{id}")
    public ResponseEntity<Void> updateArticleById(@PathVariable Long id, @Valid @RequestBody ArticleCommand articleCommand) {
        log.info("Update article with id: {}, new data: {}", id, articleCommand);
        articleService.updateArticleById(id, articleCommand);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }


    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteArticleById(@PathVariable Long id) {
        log.info("Delete article with id: {}", id);
        articleService.deleteArticleById(id);
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<ArticlesListItem>> getArticlesByAuthor(@PathVariable Long authorId) {
        log.info("Get articles by author: {}", authorId);
        List<ArticlesListItem> result = articleService.getArticlesByAuthor(authorId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ArticlesListItem>> getArticlesByCategory(@PathVariable Category category) {
        log.info("Get articles by category: {}", category);
        List<ArticlesListItem> result = articleService.getArticlesByCategory(category);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/by-keyword/{keyword}")
    public ResponseEntity<List<ArticlesListItem>> getArticlesByKeyword(@PathVariable String keyword) {
        log.info("Get articles by keyword: {}", keyword);
        List<ArticlesListItem> result = articleService.getArticlesByKeyword(keyword);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/form-init-data")
    public ResponseEntity<ArticleFormInitData>  getMovieFormInitData(){
        log.info("Get movie form init data");
        ArticleFormInitData result = articleService.getMovieFormInitData();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


}
