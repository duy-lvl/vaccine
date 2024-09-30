package fa.training.frontend.dto.response.employee;


import fa.training.frontend.util.Gender;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO representing an employee.
 * This class provides a convenient way to represent employee data in a format
 * that is suitable for API responses and other data transfer scenarios.
 *
 * @author Tran Ngoc Dinh Khanh
 * @version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmployeeListDto {

    private String id;
    private String name;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String phone;
    private String address;
    private String image;
}

