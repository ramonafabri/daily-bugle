package hu.progmasters.dailybugle.repository;


import hu.progmasters.dailybugle.domain.Article;
import hu.progmasters.dailybugle.domain.Keyword;
import hu.progmasters.dailybugle.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KeywordRepository extends JpaRepository<Keyword, Long> {

    Optional<Keyword> findByName(String name);

    List<Keyword> findAllByNameIn(List<String> names);


}
