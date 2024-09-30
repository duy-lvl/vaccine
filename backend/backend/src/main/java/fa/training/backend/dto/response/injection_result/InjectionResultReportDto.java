package fa.training.backend.dto.response.injection_result;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InjectionResultReportDto {
    private String vaccineTypeName;
    private String vaccineName;
    private String customerName;
    private LocalDate dateOfInjection;
    private int numberOfInjection;
}
