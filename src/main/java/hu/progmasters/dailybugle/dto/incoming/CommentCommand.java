package hu.progmasters.dailybugle.dto.incoming;


import hu.progmasters.dailybugle.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentCommand {


    @NotBlank(message = "Content cannot be blank")
    private String content;


}
