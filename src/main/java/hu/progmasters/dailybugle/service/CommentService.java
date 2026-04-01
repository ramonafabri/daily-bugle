package hu.progmasters.dailybugle.service;


import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Comment;
import hu.progmasters.dailybugle.domain.User;
import hu.progmasters.dailybugle.dto.incoming.CommentCommand;
import hu.progmasters.dailybugle.exception.ArticleNotFoundException;
import hu.progmasters.dailybugle.repository.ArticleRepository;
import hu.progmasters.dailybugle.repository.CommentRepository;
import hu.progmasters.dailybugle.security.CurrentUserProvider;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final ArticleService articleService;
    private final CurrentUserProvider currentUserProvider;

    private final ModelMapper modelMapper;

    @Autowired
    public CommentService(CommentRepository commentRepository, ArticleService articleService, CurrentUserProvider currentUserProvider, ModelMapper modelMapper) {
        this.commentRepository = commentRepository;
        this.articleService = articleService;
        this.currentUserProvider = currentUserProvider;
        this.modelMapper = modelMapper;
    }

    public void createComment(Long articleId, CommentCommand commentCommand) {

        User currentUser = currentUserProvider.getCurrentUser();

        Article article = articleService.findArticleById(articleId);

        Comment comment = new Comment();
        comment.setContent(commentCommand.getContent());
        comment.setArticle(article);
        comment.setAuthor(currentUser);

        commentRepository.save(comment);

        log.info("Comment created by user {} on article {}", currentUser.getId(), article.getId());

    }

}
