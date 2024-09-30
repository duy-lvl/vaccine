package fa.training.backend.dto.response.vaccine_type;

import fa.training.backend.util.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineTypeUpdateDto {
    private String id;
    private String code;
    private String name;
    private String description;
    private Status status;
    private URL imageUrl;
    private String imageName;
}
