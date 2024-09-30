package fa.training.backend.dto.response.employee;

import fa.training.backend.util.Gender;
import lombok.*;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

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
    private URL image;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeListDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(dateOfBirth, that.dateOfBirth) && gender == that.gender && Objects.equals(phone, that.phone) && Objects.equals(address, that.address) && Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dateOfBirth, gender, phone, address, image);
    }
}

