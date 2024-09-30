package fa.training.backend.dto.response.injection_schedule;

import fa.training.backend.util.InjectionScheduleStatus;
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
public class InjectionScheduleDetailDto {
    private String id;
    private String vaccineId;
    private LocalDate endDate;
    private LocalDate startDate;
    private String place;
    private String description;
    private InjectionScheduleStatus status;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectionScheduleDetailDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(vaccineId, that.vaccineId) && Objects.equals(endDate, that.endDate) && Objects.equals(startDate, that.startDate) && Objects.equals(place, that.place) && Objects.equals(description, that.description) && status == that.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vaccineId, endDate, startDate, place, description, status);
    }
}
