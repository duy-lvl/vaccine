package fa.training.backend.dto.request.vaccine;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
//@DateAfter(startDate = "timeBeginNextInjection", endDate = "timeEndNextInjection", message = "Time end next injection must be after time begin next injection")
public class VaccineAddRequest {
    @NotNull(message = "Vaccine name is required")
    @NotBlank(message = "Vaccine name is required")
    @Size(max = 50, message = "Vaccine name length must not exceed 50 characters")
    private String name;

    @NotNull(message = "Vaccine type is required")
    @NotBlank(message = "Vaccine type is required")
    private String vaccineTypeId;

    @Min(message = "Number of injection must be a positive integer", value = 1)
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

//    @NotNull(message = "Time begin next injection is required")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private LocalDate timeBeginNextInjection;
//
//    @NotNull(message = "Time end next injection is required")
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
//    private LocalDate timeEndNextInjection;

    @NotNull(message = "Origin is required")
    @NotBlank(message = "Origin is required")
    @Size(max = 50, message = "Origin name length must not exceed 200 characters")
    private String origin;
}
