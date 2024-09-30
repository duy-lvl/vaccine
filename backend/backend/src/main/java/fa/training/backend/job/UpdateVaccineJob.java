package fa.training.backend.job;

import fa.training.backend.model.InjectionSchedule;
import fa.training.backend.model.Vaccine;
import fa.training.backend.repository.InjectionScheduleRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.util.InjectionScheduleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component
public class UpdateVaccineJob {
    @Autowired
    private VaccineRepository vaccineRepository;

    @Autowired
    private InjectionScheduleRepository injectionScheduleRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional(rollbackFor = SQLException.class)
    public void updateScheduleStatus() {
        List<Vaccine> vaccines = vaccineRepository.findAll();
        LocalDate currentDate = LocalDate.now();

        Pageable pageable = PageRequest.of(0, 1);
        for (var vaccine : vaccines) {
            List<InjectionSchedule> injectionSchedules = injectionScheduleRepository
                    .findByVaccineIdAndStartDateGreaterThanOrderByStartDateAsc(vaccine.getId(), currentDate, pageable);
            if (!injectionSchedules.isEmpty()) {
                var injectionSchedule = injectionSchedules.get(0);
                if (injectionSchedule.getStatus().equals(InjectionScheduleStatus.NOT_YET)) {
                    vaccine.setTimeBeginNextInjection(injectionSchedule.getStartDate());
                    vaccine.setTimeEndNextInjection(injectionSchedule.getEndDate());
                }
            }
        }

        vaccineRepository.saveAll(vaccines);
    }
}
