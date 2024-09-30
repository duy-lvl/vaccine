package fa.training.backend.dto.request.injection_result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InjectionResultReportRequest {
    private LocalDate injectionDateFrom;
    private LocalDate injectionDateTo;
    private String vaccineTypeId;
    private String vaccineId;
    private int page = 1;
    private int size = 5;
}
