package fa.training.frontend.dto.response.injection_result;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InjectionResultDetailDto {
    private String id;

    @NotNull(message = "Vaccine is required")
    @NotBlank(message = "Vaccine is required")
    private String customerId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate injectionDate;

    private String injectionPlace;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate nextInjectionDate;

    @Min(value = 1, message = "Number of injection must be a positive integer")
    private String numberOfInjection;

    private String prevention;

    @NotNull(message = "Vaccine is required")
    @NotBlank(message = "Vaccine is required")
    private String vaccineId;
}
