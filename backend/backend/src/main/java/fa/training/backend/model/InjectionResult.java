package fa.training.backend.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Where;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Where(clause = "is_deleted = false")
public class InjectionResult {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "injection-result-id-generator")
    @GenericGenerator(name = "injection-result-id-generator", strategy = "fa.training.backend.generator.InjectionResultIdGenerator")
    private String id;

    @ManyToOne
    private Customer customer;

    private LocalDate injectionDate;
    private String injectionPlace;
    private LocalDate nextInjectionDate;
    @Column(length = 100)
    private int numberOfInjection;
    @Column(length = 100)
    private String prevention;
    @ManyToOne
    private Vaccine vaccine;
    private boolean isDeleted = false;

    public InjectionResult(String id, Customer customer, LocalDate injectionDate, String injectionPlace, LocalDate nextInjectionDate, int numberOfInjection, String prevention, Vaccine vaccine) {
        this.id = id;
        this.customer = customer;
        this.injectionDate = injectionDate;
        this.injectionPlace = injectionPlace;
        this.nextInjectionDate = nextInjectionDate;
        this.numberOfInjection = numberOfInjection;
        this.prevention = prevention;
        this.vaccine = vaccine;
        this.isDeleted = false;
    }
}
