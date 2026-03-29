package hu.progmasters.dailybugle.dto.outgoing;

import hu.progmasters.dailybugle.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleFormInitData {

    private List<CategoryDetails> categories;

}
