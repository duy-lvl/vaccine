package fa.training.backend.dto.response.employee;

import fa.training.backend.util.Gender;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDetailDto {
    private String id;
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String phone;
    private String address;
    private String email;
    private String workPlace;
    private String position;
    private URL imageUrl;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EmployeeDetailDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && gender == that.gender && Objects.equals(dateOfBirth, that.dateOfBirth) && Objects.equals(phone, that.phone) && Objects.equals(address, that.address) && Objects.equals(email, that.email) && Objects.equals(workPlace, that.workPlace) && Objects.equals(position, that.position);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, gender, dateOfBirth, phone, address, email, workPlace, position);
    }

}
