package fa.training.backend.dto.response.customer;


import fa.training.backend.util.Gender;
import fa.training.backend.util.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

// return data to frontend
public class CustomerDto {
    private String id;
    private String address;
    private LocalDate dateOfBirth;
    private String email;
    private String fullName;
    private Gender gender;
    private String identityCard;
    private String phone;
    private String username;
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CustomerDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(address, that.address) && Objects.equals(dateOfBirth, that.dateOfBirth) && Objects.equals(email, that.email) && Objects.equals(fullName, that.fullName) && gender == that.gender && Objects.equals(identityCard, that.identityCard) && Objects.equals(phone, that.phone) && Objects.equals(username, that.username) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, dateOfBirth, email, fullName, gender, identityCard, phone, username, status);
    }
}
