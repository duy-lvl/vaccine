package fa.training.backend.model;

import fa.training.backend.util.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Where(clause = "is_deleted = false")
public class VaccineType {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "vaccine-type-id-generator")
    @GenericGenerator(name = "vaccine-type-id-generator", strategy = "fa.training.backend.generator.VaccineTypeIdGenerator")
    private String id;
    @Column(unique = true)
    private String code;
    @Column(length = 200)
    private String description;
    @Column(length = 50)
    private String name;
    private String imageUrl;
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean isDeleted = false;

    public VaccineType(String id) {
        this.id = id;
    }

    public VaccineType(String id, String code, String description, String name, String imageUrl, Status status) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.name = name;
        this.imageUrl = imageUrl;
        this.status = status;
        this.isDeleted = false;
    }
}
