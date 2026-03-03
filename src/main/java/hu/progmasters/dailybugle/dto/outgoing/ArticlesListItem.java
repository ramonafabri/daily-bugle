package hu.progmasters.dailybugle.dto.outgoing;

import hu.progmasters.dailybugle.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticlesListItem {

    private Long id;

    private String author;

    private String title;

    private String synopsis;

    private Long commentCount;

    private BigDecimal averageRating;

    private long ratingCount;

    private Category category;

    List<String> keywords;

}
