package fa.training.backend.dto.request.vaccine;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VaccineInjectionScheduleRequestDto {
    private String id;
    private String name;
}
