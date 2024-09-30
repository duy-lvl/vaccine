package fa.training.backend.dto.request.injection_schedule;

import fa.training.backend.validator.DateAfter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DateAfter(startDate = "startDate", endDate = "endDate", message = "End date must be after start date")
public class InjectionScheduleUpdateRequest {
    @NotNull(message = "Vaccine Id is required")
    @NotBlank(message = "Vaccine Id is required")
    @Size(max = 36, message = "Vaccine Id must not exceed 36 characters!")
    private String vaccineId;

    @NotNull(message = "Start date is required")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @DateTimeFormat(iso=DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @NotNull(message = "Place is required")
    @NotBlank(message = "Place is required")
    @Size(max = 200, message = "Place must not exceed 200 characters!")
    private String place;

    @NotNull(message = "Description is required")
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description must not exceed 200 characters!")
    private String description;
}
