package fa.training.backend.dto.response;

import fa.training.backend.util.Role;
import lombok.*;

import java.io.Serializable;
import java.net.URL;
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
    private URL avatar;
}
