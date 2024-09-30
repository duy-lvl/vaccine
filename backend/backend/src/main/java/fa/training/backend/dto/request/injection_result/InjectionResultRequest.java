package fa.training.backend.dto.request.injection_result;

import fa.training.backend.model.Customer;
import fa.training.backend.model.InjectionResult;
import fa.training.backend.model.Vaccine;
import fa.training.backend.validator.DateAfter;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DateAfter(startDate = "injectionDate", endDate = "nextInjectionDate", message = "Next injection date must be after previous injection")
public class InjectionResultRequest {
    @NotNull(message = "Customer Id is required")
    @NotBlank(message = "Customer Id is required")
    @Size(max = 100, message = "Customer Id must not exceed 100 characters!")
    private String customerId;

    @NotNull(message = "Injection date is required")
    private LocalDate injectionDate;

    @NotNull(message = "Injection place is required")
    @NotBlank(message = "Injection place is required")
    @Size(max = 255, message = "Injection place must not exceed 100 characters!")
    private String injectionPlace;

    private LocalDate nextInjectionDate;

    @Min(value = 1, message = "Number of injection must be a positive integer")
    private int numberOfInjection;


    private String prevention;

    @NotNull(message = "Vaccine is required")
    @NotBlank(message = "Vaccine is required")
    private String vaccineId;

    public InjectionResult toEntity() {
        return new InjectionResult(
                "",
                new Customer(customerId),
                injectionDate,
                injectionPlace,
                nextInjectionDate,
                numberOfInjection,
                prevention,
                new Vaccine(vaccineId)
        );
    }
}
