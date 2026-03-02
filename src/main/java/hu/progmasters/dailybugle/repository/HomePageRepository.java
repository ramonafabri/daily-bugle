package hu.progmasters.dailybugle.repository;


import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HomePageRepository extends JpaRepository<Article, Long> {
    @Query("""
    SELECT a
    FROM Article a
    LEFT JOIN a.ratings r
    WHERE a.status = :status
    AND (a.publishAt IS NULL OR a.publishAt <= :now)
    GROUP BY a
    ORDER BY AVG(r.value) DESC
""")
    List<Article> findTopRatedPublic(
            @Param("status") Status status,
            @Param("now") LocalDateTime now,
            Pageable pageable
    );



}
