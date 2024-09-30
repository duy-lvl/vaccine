package fa.training.frontend.dto.request.injection_reasult;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
public class InjectionResultDto {
    private String id;
    private String customer;
    private LocalDate injectionDate;
    private LocalDate nextInjectionDate;
    private int numberOfInjection;
    private String prevention;
    private String vaccineName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectionResultDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(customer, that.customer) && Objects.equals(injectionDate, that.injectionDate) && Objects.equals(nextInjectionDate, that.nextInjectionDate) && Objects.equals(numberOfInjection, that.numberOfInjection) && Objects.equals(prevention, that.prevention) && Objects.equals(vaccineName, that.vaccineName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, injectionDate, nextInjectionDate, numberOfInjection, prevention, vaccineName);
    }
}
