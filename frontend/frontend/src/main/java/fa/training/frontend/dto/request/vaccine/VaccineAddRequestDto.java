package fa.training.frontend.dto.request.vaccine;

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
@AllArgsConstructor
@NoArgsConstructor
public class VaccineAddRequestDto {
    @NotNull(message = "Vaccine name is required")
    @NotBlank(message = "Vaccine name is required")
    @Size(max = 50, message = "Vaccine name length must not exceed 50 characters")
    private String name;

    @NotNull(message = "Vaccine type is required")
    @NotBlank(message = "Vaccine type is required")
    private String vaccineTypeId;

    @NotNull(message = "Number of injection is required")
    @NotBlank(message = "Number of injection is required")
    @Min(message = "Number of injection must be a positive integer", value = 1)
    @Size(max = 10, message = "Number of injection must not exceed 10 characters")
    private int numberOfInjection;

    @NotNull(message = "Usage is required")
    @NotBlank(message = "Usage is required")
    @Size(max = 200, message = " Usage length must not exceed 200 characters")
    private String usage;

    @NotNull(message = "Indication is required")
    @NotBlank(message = "Indication is required")
    @Size(max = 200, message = "Indication length must not exceed 200 characters")
    private String indication;

    @NotNull(message = "Contraindication is required")
    @NotBlank(message = "Contraindication is required")
    @Size(max = 200, message = "Contraindication name length must not exceed 200 characters")
    private String contraindication;

    @NotNull(message = "Origin is required")
    @NotBlank(message = "Origin is required")
    @Size(max = 50, message = "Origin name length must not exceed 200 characters")
    private String origin;
}
