package fa.training.backend.dto.response.vaccine;

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

public class VaccineUpdateDto {
    private String id;
    private String name;
    private String vaccineTypeId;
    private int numberOfInjection;
    private String usage;
    private String indication;
    private String contraindication;
    private LocalDate timeBeginNextInjection;
    private LocalDate timeEndNextInjection;
    private String origin;
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VaccineUpdateDto that)) return false;
        return numberOfInjection == that.numberOfInjection && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(vaccineTypeId, that.vaccineTypeId) && Objects.equals(usage, that.usage) && Objects.equals(indication, that.indication) && Objects.equals(contraindication, that.contraindication) && Objects.equals(timeBeginNextInjection, that.timeBeginNextInjection) && Objects.equals(timeEndNextInjection, that.timeEndNextInjection) && Objects.equals(origin, that.origin) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, vaccineTypeId, numberOfInjection, usage, indication, contraindication, timeBeginNextInjection, timeEndNextInjection, origin, status);
    }
}
