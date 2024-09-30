package fa.training.backend.repository;

import fa.training.backend.model.InjectionSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InjectionScheduleRepository extends JpaRepository<InjectionSchedule, String> {
    Page<InjectionSchedule> findByVaccineNameContainsIgnoreCase(String name, Pageable pageable);

    Page<InjectionSchedule> findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndVaccineVaccineTypeId(LocalDate dateFrom, LocalDate dateTo, String vaccineTypeId, Pageable pageable);


    List<InjectionSchedule> findByVaccineIdAndStartDateGreaterThanOrderByStartDateAsc(
            String vaccineId,
            LocalDate currentDate,
            Pageable pageable
    );
}
