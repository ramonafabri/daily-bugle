package hu.progmasters.dailybugle.controller;


import hu.progmasters.dailybugle.dto.incoming.ArticleCommand;
import hu.progmasters.dailybugle.dto.incoming.CommentCommand;
import hu.progmasters.dailybugle.service.CommentService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@Slf4j
public class CommentController {

    private final CommentService commentService;


    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }


    @PostMapping
    public ResponseEntity<Void> createComment( @PathVariable Long articleId, @Valid @RequestBody CommentCommand commentCommand) {
        log.info("Create comment for article {}: {}", articleId, commentCommand);
        commentService.createComment(articleId,commentCommand);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
