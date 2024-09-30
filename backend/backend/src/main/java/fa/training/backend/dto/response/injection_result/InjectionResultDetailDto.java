package fa.training.backend.dto.response.injection_result;

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
public class InjectionResultDetailDto {
    private String id;
    private String customerId;
    private LocalDate injectionDate;
    private String injectionPlace;
    private LocalDate nextInjectionDate;
    private int numberOfInjection;
    private String prevention;
    private String vaccineId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectionResultDetailDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(customerId, that.customerId) && Objects.equals(injectionDate, that.injectionDate) && Objects.equals(injectionPlace, that.injectionPlace) && Objects.equals(nextInjectionDate, that.nextInjectionDate) && Objects.equals(numberOfInjection, that.numberOfInjection) && Objects.equals(prevention, that.prevention) && Objects.equals(vaccineId, that.vaccineId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customerId, injectionDate, injectionPlace, nextInjectionDate, numberOfInjection, prevention, vaccineId);
    }
}
