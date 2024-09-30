package fa.training.backend.model;

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
@Where(clause = "is_deleted = false")
public class Vaccine {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "vaccine-id-generator")
    @GenericGenerator(name = "vaccine-id-generator", strategy = "fa.training.backend.generator.VaccineIdGenerator")
    private String id;
    @Column(length = 200)
    private String contraindication;
    @Column(length = 200)
    private String indication;
    private int numberOfInjection;
    @Column(length = 50)
    private String origin;
    private LocalDate timeBeginNextInjection;
    private LocalDate timeEndNextInjection;
    @Column(length = 200, name = "vaccine_usage")
    private String usage;
    @Column(length = 100, name = "vaccine_name")
    private String name;
    @ManyToOne
    private VaccineType vaccineType;

    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean isDeleted = false;

    public Vaccine(String id) {
        this.id = id;
    }

    public Vaccine(String id, String contraindication, String indication, int numberOfInjection, String origin, LocalDate timeBeginNextInjection, LocalDate timeEndNextInjection, String usage, String name, VaccineType vaccineType, Status status) {
        this.id = id;
        this.contraindication = contraindication;
        this.indication = indication;
        this.numberOfInjection = numberOfInjection;
        this.origin = origin;
        this.timeBeginNextInjection = timeBeginNextInjection;
        this.timeEndNextInjection = timeEndNextInjection;
        this.usage = usage;
        this.name = name;
        this.vaccineType = vaccineType;
        this.status = status;
        this.isDeleted = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vaccine vaccine)) return false;
        return numberOfInjection == vaccine.numberOfInjection && Objects.equals(id, vaccine.id) && Objects.equals(contraindication, vaccine.contraindication) && Objects.equals(indication, vaccine.indication) && Objects.equals(origin, vaccine.origin) && Objects.equals(timeBeginNextInjection, vaccine.timeBeginNextInjection) && Objects.equals(timeEndNextInjection, vaccine.timeEndNextInjection) && Objects.equals(usage, vaccine.usage) && Objects.equals(name, vaccine.name) && Objects.equals(vaccineType, vaccine.vaccineType) && status == vaccine.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, contraindication, indication, numberOfInjection, origin, timeBeginNextInjection, timeEndNextInjection, usage, name, vaccineType, status);
    }

    @Override
    public String toString() {
        return "Vaccine{" +
                "contraindication: '" + contraindication + '\'' +
                ", indication: '" + indication + '\'' +
                ", number of injection: " + numberOfInjection +
                ", origin: '" + origin + '\'' +
                ", time begin next injection: " + timeBeginNextInjection +
                ", time end next injection: " + timeEndNextInjection +
                ", usage: '" + usage + '\'' +
                ", name: '" + name + '\'' +
                ", vaccine type id: " + vaccineType.getId() +
                '}';
    }
}
