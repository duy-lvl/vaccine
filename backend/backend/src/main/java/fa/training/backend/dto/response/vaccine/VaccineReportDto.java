package fa.training.backend.dto.response.vaccine;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineReportDto {
    private String vaccineName;
    private String vaccineType;
    private long numberOfInjection;
    private LocalDate timeBeginNextInjection;
    private LocalDate timeEndNextInjection;
    private String origin;
}
