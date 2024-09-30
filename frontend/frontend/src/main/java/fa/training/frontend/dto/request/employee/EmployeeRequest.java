package fa.training.frontend.dto.request.employee;

import fa.training.frontend.util.Gender;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeRequest {

    @NotBlank(message = "Name must not be empty!")
    @NotNull(message = "Name is required!")
    @Size(max = 100, message = "Name length must not exceed 100 characters!")
    private String name;

    @NotNull(message = "Gender is required!")
    private Gender gender;

    @NotNull(message = "Date of birth is required!")
    @Past(message = "Date of birth must be in the past!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Phone must not be empty!")
    @NotNull(message = "Phone is required!")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Phone number must be 10 or 11 digits starting with 0!")
    private String phone;

    @NotBlank(message = "Address must not be empty!")
    @NotNull(message = "Address is required!")
    @Size(max = 100, message = "Address length must not exceed 100 characters!")
    private String address;

    @NotBlank(message = "Email must not be empty!")
    @NotNull(message = "Email is required!")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Wrong email!")
    @Size(max = 100, message = "Email length must not exceed 100 characters!")
    private String email;

    @Size(max = 100, message = "Working place's length must not exceed 100 characters!")
    private String workPlace;

    @Size(max = 100, message = "Position's length must not exceed 100 characters!")
    private String position;

    private String urlOfImage;

    @NotBlank(message = "Username must be not empty!")
    @NotNull(message = "Username is required!")
    @Size(max = 255, message = "Username length must not exceed 255 characters!")
    private String username;

    @NotBlank(message = "Password must be not empty!")
    @NotNull(message = "Password is required!")
    @Size(max = 72, message = "Password length must not exceed 72 characters!")
    private String password;

}
