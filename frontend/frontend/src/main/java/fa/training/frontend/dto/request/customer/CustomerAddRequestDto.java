package fa.training.frontend.dto.request.customer;

import fa.training.frontend.util.Gender;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

// send data to backend
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerAddRequestDto {

    @NotBlank(message = "Full name must not be null")
    @NotNull(message = "Full name must not be null")
    @Size(max = 100, message = "Full name length must not exceed 100 characters")
    private String fullName;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Date of birth card must not be null")
    private LocalDate dateOfBirth;

    @NotBlank(message = "Gender must not be null")
    @NotNull(message = "Gender must not be null")
    @Size(max = 10, message = "Gender must not exceed 10 characters")
    private Gender gender;

    @NotBlank(message = "Identity card must not be null")
    @NotNull(message = "Identity card must not be null")
    @Pattern(regexp = "^\\d{12}$", message = "Identity card must be a string of 10 digits")
    private String identityCard;

    @NotBlank(message = "Address must not be null")
    @NotNull(message = "Address card must not be null")
    @Size(max = 100, message = "Address length must not exceed 100 characters")
    private String address;

    @NotBlank(message = "Username must not be null")
    @NotNull(message = "Username must not be null")
    @Size(max = 10, message = "Username length must not exceed 10 characters")
    private String username;

    @NotBlank(message = "Password must not be null")
    @NotNull(message = "Password card must not be null")
    @Size(max = 20, message = "password length must not exceed 20 characters")
    private String password;


    @NotBlank(message = "Email must not be null")
    @NotNull(message = "Email must not be null")
    @Email(message = "Invalid email")
    @Size(max = 25, message = "Email length must not exceed 25 characters")
    private String email;

    @NotBlank(message = "Phone must not be null")
    @NotNull(message = "Phone must not be null")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must be 10 or 11 digits starting with 0")
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerAddRequestDto that)) return false;
        return Objects.equals(address, that.address) && Objects.equals(dateOfBirth, that.dateOfBirth) && Objects.equals(email, that.email) && Objects.equals(fullName, that.fullName) && Objects.equals(password, that.password) && gender == that.gender && Objects.equals(identityCard, that.identityCard) && Objects.equals(phone, that.phone) && Objects.equals(username, that.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(address, dateOfBirth, email, fullName, password, gender, identityCard, phone, username);
    }
}
