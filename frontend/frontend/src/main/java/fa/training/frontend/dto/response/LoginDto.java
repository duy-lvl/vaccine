package fa.training.frontend.dto.response;

import fa.training.frontend.util.Role;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto implements Serializable {
    private String username;
    private List<Role> roles;
    private String jwt;
    private String name;
    private String avatar;
}