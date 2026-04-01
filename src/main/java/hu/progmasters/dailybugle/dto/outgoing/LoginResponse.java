package hu.progmasters.dailybugle.dto.outgoing;

import hu.progmasters.dailybugle.domain.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private Long id;
    private String displayName;
    private Role role;
    private String token;
}
