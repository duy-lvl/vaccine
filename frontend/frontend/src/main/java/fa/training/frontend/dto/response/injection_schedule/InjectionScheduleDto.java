package fa.training.frontend.dto.response.injection_schedule;

import fa.training.frontend.util.InjectionScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InjectionScheduleDto {
    private String id;
    private String vaccineName;
    private String time;
    private String place;
    private InjectionScheduleStatus status;
    private String description;
}
