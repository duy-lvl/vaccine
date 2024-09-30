package fa.training.frontend.dto.response.injection_schedule;

import fa.training.frontend.util.InjectionScheduleStatus;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class InjectionScheduleDetailDto {
    private String id;
    private String vaccineId;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate startDate;
    private String place;
    private String description;
    private InjectionScheduleStatus status;
}