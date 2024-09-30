package fa.training.frontend.dto.request.injection_reasult;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InjectionResultAddRequest {
    @NotNull(message = "Injection date is required")
    @PastOrPresent(message = "Injection date cannot be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate injectionDate;
    @NotBlank(message = "Vaccine type ID is required")
    private String vaccineTypeId;

    private String searchCustomer;
    private String customerId;

    public String toQuery() {
        return String.format("injectionDate=%s&vaccineTypeId=%s", injectionDate, vaccineTypeId);
    }
}
