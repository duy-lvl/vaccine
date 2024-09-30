package fa.training.frontend.dto.response.injection_result;

import fa.training.frontend.dto.request.vaccine.VaccineInjectionScheduleRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InjectionScheduleResponse {
    private String place;
    private VaccineInjectionScheduleRequestDto vaccine;
}
