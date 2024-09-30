package fa.training.backend.model;

import fa.training.backend.util.Gender;
import fa.training.backend.util.Role;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Where(clause = "is_deleted = false")
public class Employee implements UserDetails {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "employee-id-generator")
    @GenericGenerator(name = "employee-id-generator", strategy = "fa.training.backend.generator.EmployeeIdGenerator")
    private String id;
    private String address;
    private LocalDate dateOfBirth;
    @Column(length = 100)
    private String email;
    @Column(length = 100)
    private String name;
    private Gender gender;
    private String image;
    private String password;
    @Column(length = 20)
    private String phone;
    @Column(length = 100)
    private String position;
    private String username;
    private String workPlace;

    private boolean isDeleted = false;

    public Employee(String id, String address, LocalDate dateOfBirth, String email, String name, Gender gender, String image, String password, String phone, String position, String username, String workPlace) {
        this.id = id;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.name = name;
        this.gender = gender;
        this.image = image;
        this.password = password;
        this.phone = phone;
        this.position = position;
        this.username = username;
        this.workPlace = workPlace;
        this.isDeleted = false;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(Role.EMPLOYEE.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
