package fa.training.frontend.dto.request.vaccine;

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
