package fa.training.frontend.dto.request.employee;

import fa.training.frontend.util.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URL;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeUpdateDto {
    @NotBlank(message = "Name must be not null")
    @NotNull
    @Size(max = 12, message = "ID length must not exceed 12 characters")
    private String id;

    @NotBlank(message = "Address must not be empty!")
    @NotNull(message = "Address is required!")
    @Size(max = 100, message = "Address length must not exceed 100 characters!")
    private String address;

    @NotNull(message = "Date of birth is required!")
    @Past(message = "Date of birth must be in the past!")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;

    @NotBlank(message = "Email must not be empty!")
    @NotNull(message = "Email is required!")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "Wrong email!")
    @Size(max = 100, message = "Email length must not exceed 100 characters!")
    private String email;

    @NotBlank(message = "Name must not be empty!")
    @NotNull(message = "Name is required!")
    @Size(max = 100, message = "Name length must not exceed 100 characters!")
    private String name;

    @NotNull(message = "Gender is required!")
    private Gender gender;


    @NotBlank(message = "Phone must not be empty!")
    @NotNull(message = "Phone is required!")
    @Pattern(regexp = "^0\\d{9,10}$", message = "Wrong phone number")
    private String phone;

    private String position;

    @NotBlank(message = "Username must be not empty!")
    @NotNull(message = "Username is required!")
    private String username;

    @Size(max = 100, message = "Working place's length must not exceed 100 characters!")
    private String workPlace;

    private URL urlImage;
    private String image;

}
