package fa.training.frontend.dto.request.vaccine_type;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineTypeRequestDto {
    private String code;
    private String name;

}
