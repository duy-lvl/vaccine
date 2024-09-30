package fa.training.backend.model;

import fa.training.backend.util.Gender;
import fa.training.backend.util.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.Objects;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Where(clause = "is_deleted = false")
public class Customer {
    @Id
    @GeneratedValue(generator = "customer-id-generator")
    @GenericGenerator(name = "customer-id-generator", strategy = "fa.training.backend.generator.CustomerIdGenerator")
    @Column(length = 36)
    private String id;

    private String address;

    private LocalDate dateOfBirth;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String fullName;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(length = 12)
    private String identityCard;

    private String password;

    @Column(length = 20)
    private String phone;

    private String username;

    @Enumerated(EnumType.STRING)
    private Status status;

    private boolean isDeleted = false;

    public Customer(String id) {
        this.id = id;
    }

    public Customer(String id, String address, LocalDate dateOfBirth, String email, String fullName, Gender gender, String identityCard, String password, String phone, String username, Status status) {
        this.id = id;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.fullName = fullName;
        this.gender = gender;
        this.identityCard = identityCard;
        this.password = password;
        this.phone = phone;
        this.username = username;
        this.status = status;
        this.isDeleted = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return Objects.equals(id, customer.id) && Objects.equals(address, customer.address) && Objects.equals(dateOfBirth, customer.dateOfBirth) && Objects.equals(email, customer.email) && Objects.equals(fullName, customer.fullName) && gender == customer.gender && Objects.equals(identityCard, customer.identityCard) && Objects.equals(password, customer.password) && Objects.equals(phone, customer.phone) && Objects.equals(username, customer.username) && status == customer.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, address, dateOfBirth, email, fullName, gender, identityCard, password, phone, username, status);
    }
}

