package fa.training.backend.dto.request.customer;

import fa.training.backend.util.Gender;
import jakarta.validation.constraints.*;
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

// send add data to backend
public class CustomerAddRequest {

    @NotBlank(message = "Full name must not be null")
    @NotNull(message = "Full name must not be null")
    @Size(max = 100, message = "Full name length must not exceed 100 characters")
    private String fullName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Date of birth must not be null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender must not be null")
    private Gender gender;

    @NotBlank(message = "Identity card must not be null")
    @NotNull(message = "Identity card must not be null")
    @Pattern(regexp = "^\\d{12}$", message = "Identity card must be a string of 12 digits")
    private String identityCard;

    @NotBlank(message = "Address must not be null")
    @NotNull(message = "Address must not be null")
    @Size(max = 100, message = "Address length must not exceed 100 characters")
    private String address;

    @NotBlank(message = "Username must not be null")
    @NotNull(message = "Username must not be null")
    @Size(max = 255, message = "Username length must not exceed 255 characters")
    private String username;

    @NotBlank(message = "Password must not be null")
    @NotNull(message = "Password must not be null")
    @Size(min = 6, max = 20, message = "Password must be at least 6 characters")
    private String password;


    @NotBlank(message = "Email must not be null")
    @NotNull(message = "Email must not be null")
    @Email(message = "Invalid email")
    @Size(max = 25, message = "Email length must not exceed 12 characters")
    private String email;

    @NotBlank(message = "Phone must not be null")
    @NotNull(message = "Phone must not be null")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must be 10 or 11 digits starting with 0")
    private String phone;

}
