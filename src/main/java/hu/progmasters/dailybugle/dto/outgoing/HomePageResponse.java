package hu.progmasters.dailybugle.dto.outgoing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HomePageResponse {

    private List<ArticlesListItem> latest = new ArrayList<>();
    private List<ArticlesListItem> topRated = new ArrayList<>();
    private List<ArticlesListItem> topRatedLast3Days = new ArrayList<>();


}
