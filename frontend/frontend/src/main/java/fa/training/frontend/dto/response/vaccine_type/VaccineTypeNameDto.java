package fa.training.frontend.dto.response.vaccine_type;

import fa.training.frontend.dto.response.vaccine.VaccineNameDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineTypeNameDto {
    private String id;
    private String name;
    private List<VaccineNameDto> vaccines;
}