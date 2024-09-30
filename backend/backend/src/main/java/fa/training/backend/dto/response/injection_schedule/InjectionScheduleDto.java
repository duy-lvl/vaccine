package fa.training.backend.dto.response.injection_schedule;

import fa.training.backend.util.InjectionScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InjectionScheduleDto {
    private String id;
    private String vaccineName;
    private String time;
    private String place;
    private InjectionScheduleStatus status;
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InjectionScheduleDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(vaccineName, that.vaccineName) && Objects.equals(time, that.time) && Objects.equals(place, that.place) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, vaccineName, time, place, description);
    }
}
