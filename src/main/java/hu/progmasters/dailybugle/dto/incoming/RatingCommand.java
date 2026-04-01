package hu.progmasters.dailybugle.dto.incoming;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingCommand {

    @NotNull(message = "Value cannot be null")
    @Min(1)
    @Max(5)
    private Integer value;

}
