package fa.training.backend.dto.response.vaccine;

import fa.training.backend.util.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VaccineIndividualDto {
    private String id;
    private String name;
    private String vaccineTypeName;
    private int numberOfInjection;
    private String origin;
    private Status status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VaccineIndividualDto that)) return false;
        return numberOfInjection == that.numberOfInjection && Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(vaccineTypeName, that.vaccineTypeName) && Objects.equals(origin, that.origin) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, vaccineTypeName, numberOfInjection, origin, status);
    }
}
