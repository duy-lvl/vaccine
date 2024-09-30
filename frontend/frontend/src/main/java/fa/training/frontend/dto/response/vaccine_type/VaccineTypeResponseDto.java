package fa.training.frontend.dto.response.vaccine_type;

import fa.training.frontend.util.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class VaccineTypeResponseDto {

    private String id;
    @NotNull(message = "Vaccine type code is required")
    @NotBlank(message = "Vaccine type code is required")
    @Size(max = 50, message = "Vaccine type code length must not exceed 50 characters")
    private String code;
    @NotNull(message = "Vaccine type name is required")
    @NotBlank(message = "Vaccine type name is required")
    @Size(max = 50, message = "Vaccine type name length must not exceed 50 characters")
    private String name;
    @NotNull(message = "Description is required")
    @NotBlank(message = "Description is required")
    @Size(max = 200, message = "Description's length must not exceed 200 characters")
    private String description;
    @NotNull(message = "Status is required")
    private Status status;
    private URL imageUrl;
    private String imageName;
}
