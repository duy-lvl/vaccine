package fa.training.backend.model;

import fa.training.backend.util.InjectionScheduleStatus;
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
public class InjectionSchedule {
    @Id
    @Column(length = 36)
    @GeneratedValue(generator = "injection-schedule-id-generator")
    @GenericGenerator(name = "injection-schedule-id-generator", strategy = "fa.training.backend.generator.InjectionScheduleIdGenerator")
    private String id;
    @Column(length = 1000)
    private String description;
    private LocalDate startDate;
    private String place;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private InjectionScheduleStatus status;
    @ManyToOne
    private Vaccine vaccine;
    private boolean isDeleted = false;

    public InjectionSchedule(String id, String description, LocalDate startDate, String place, LocalDate endDate, InjectionScheduleStatus status, Vaccine vaccine) {
        this.id = id;
        this.description = description;
        this.startDate = startDate;
        this.place = place;
        this.endDate = endDate;
        this.status = status;
        this.vaccine = vaccine;
        this.isDeleted = false;
    }
}
