package fa.training.frontend.dto.response.vaccine;

import fa.training.frontend.util.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineResponseDto {
    private String id;
    private String name;
    private String vaccineTypeName;
    private int numberOfInjection;
    private String origin;
    private Status status;
}
