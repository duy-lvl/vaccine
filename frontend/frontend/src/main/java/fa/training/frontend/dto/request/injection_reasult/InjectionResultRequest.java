package fa.training.frontend.dto.request.injection_reasult;

import fa.training.frontend.validator.DateAfter;
import jakarta.validation.constraints.Min;
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
@DateAfter(startDate = "injectionDate", endDate = "nextInjectionDate", message = "Next injection date must be after previous injection")
public class InjectionResultRequest {
    @NotNull(message = "Customer is required")
    @NotBlank(message = "Customer is required")
    @Size(max = 100, message = "Customer Id must not exceed 100 characters!")
    private String customerId;

    @NotNull(message = "Injection date is required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate injectionDate;

    @NotNull(message = "Injection place is required")
    @NotBlank(message = "Injection place is required")
    @Size(max = 255, message = "Injection place must not exceed 100 characters!")
    private String injectionPlace;

    @Min(value = 1, message = "Number of injection must be a positive integer")
    private int numberOfInjection = 1;


    @NotNull(message = "Vaccine is required")
    @NotBlank(message = "Vaccine is required")
    private String vaccineId;

}
