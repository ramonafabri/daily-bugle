package hu.progmasters.dailybugle.repository;

import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long> {

    List<Comment> findByAuthor_Id(Long userId);
}
