package fa.training.backend.job;

import fa.training.backend.model.InjectionSchedule;
import fa.training.backend.repository.InjectionScheduleRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.util.InjectionScheduleStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class UpdateScheduleJob {
    @Autowired
    private InjectionScheduleRepository injectionScheduleRepository;

    @Autowired
    private VaccineRepository vaccineRepository;

    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Ho_Chi_Minh")
    @Transactional
    public void updateScheduleStatus() {
        List<InjectionSchedule> schedules = injectionScheduleRepository.findAll();

        for (var schedule : schedules) {
            schedule.setStatus(checkStatus(schedule.getStartDate(), schedule.getEndDate()));
            if (!schedule.getStatus().equals(InjectionScheduleStatus.NOT_YET)) {
                var vaccine = schedule.getVaccine();
                vaccine.setTimeEndNextInjection(null);
                vaccine.setTimeBeginNextInjection(null);
                vaccineRepository.save(vaccine);
            }
        }

        injectionScheduleRepository.saveAll(schedules);
    }

    private InjectionScheduleStatus checkStatus(LocalDate from, LocalDate to) {
        LocalDate now = LocalDate.now();
        if (from.isAfter(now)) {
            return InjectionScheduleStatus.NOT_YET;
        }
        if (from.isBefore(now) && to.isAfter(now)) {
            return InjectionScheduleStatus.OPEN;
        }
        return InjectionScheduleStatus.OVER;
    }
}
