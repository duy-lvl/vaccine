package fa.training.frontend.dto.request.vaccine_type;

import fa.training.frontend.util.Status;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateVaccineTypeRequest {
    private String code;
    private String name;
    private String description;
    private String imageUrl;
    private Status status;
   
}
