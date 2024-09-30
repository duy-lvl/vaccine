package fa.training.frontend.dto.response.customer;

import fa.training.frontend.util.Gender;
import fa.training.frontend.util.Status;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

// receive data from backend
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

// receive data from backend
public class CustomerResponseDto {
    private String id;

    @NotBlank(message = "Address must be not null")
    @NotNull(message = "Address card must be not null")
    @Size(max = 100, message = "Address length must not exceed 100 characters")
    private String address;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email must be not null")
    @NotNull(message = "Email must be not null")
    @Email(message = "Invalid email")
    @Size(max = 25, message = "Email length must not exceed 25 characters")
    private String email;

    @NotBlank(message = "Full name must be not null")
    @NotNull(message = "Full name must be not null")
    @Size(max = 100, message = "Full name length must not exceed 100 characters")
    private String fullName;
    private Gender gender;

    @NotBlank(message = "Identity card must be not null")
    @NotNull(message = "Identity card must be not null")
    @Pattern(regexp = "^\\d{10}$", message = "Identity card must be a string of 10 digits")
    private String identityCard;

    @NotBlank(message = "Phone must be not null")
    @NotNull(message = "Phone must be not null")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must be 10 or 11 digits starting with 0")
    private String phone;

    @NotBlank(message = "Username must be not null")
    @NotNull(message = "Username must be not null")
    @Size(max = 10, message = "Username length must not exceed 10 characters")
    private String username;
    private Status status;
}
