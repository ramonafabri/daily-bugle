package hu.progmasters.dailybugle.repository;


import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    List<Article> findByStatus(Status status);

    Optional<Article> findByIdAndStatus(Long id, Status status);

    List<Article> findByAuthorIdAndStatus(Long authorId, Status status);

    List<Article> findByAuthor_IdOrderByCreatedAtDesc(Long userId);


    @Query("""
                SELECT a FROM Article a
                WHERE a.id = :id
                AND a.status = :status
                AND (a.publishAt IS NULL OR a.publishAt <= :now)
            """)
    Optional<Article> findPublicById(
            @Param("id") Long id,
            @Param("status") Status status,
            @Param("now") LocalDateTime now
    );

    @Query("""
                SELECT a FROM Article a
                WHERE a.author.id = :authorId
                AND a.status = :status
                AND (a.publishAt IS NULL OR a.publishAt <= :now)
                ORDER BY a.createdAt DESC
            """)
    List<Article> findPublicByAuthor(
            @Param("authorId") Long authorId,
            @Param("status") Status status,
            @Param("now") LocalDateTime now
    );

}
