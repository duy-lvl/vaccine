package fa.training.backend.service.impl;

import fa.training.backend.dto.request.injection_result.InjectionResultAddRequest;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDetailDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.backend.exception.common.NotFoundException;
import fa.training.backend.model.InjectionSchedule;
import fa.training.backend.model.Vaccine;
import fa.training.backend.repository.InjectionScheduleRepository;
import fa.training.backend.repository.VaccineRepository;
import fa.training.backend.service.InjectionScheduleService;
import fa.training.backend.util.InjectionScheduleStatus;
import fa.training.backend.util.Message;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
public class InjectionScheduleServiceImpl implements InjectionScheduleService {
    private final InjectionScheduleRepository injectionScheduleRepository;
    private final VaccineRepository vaccineRepository;
    private ModelMapper modelMapper;

    @Autowired
    public InjectionScheduleServiceImpl(VaccineRepository vaccineRepository, InjectionScheduleRepository injectionScheduleRepository, ModelMapper modelMapper) {
        this.injectionScheduleRepository = injectionScheduleRepository;
        this.vaccineRepository = vaccineRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    @Transactional(rollbackFor = SQLException.class)
    public boolean add(InjectionScheduleAddRequest request) {

        if (!vaccineRepository.existsById(request.getVaccineId())) {
            throw new NotFoundException(Message.MSG_153, request.getVaccineId());
        }

        var injectionSchedule = modelMapper.map(request, InjectionSchedule.class);
        injectionSchedule.setId(null);
        injectionSchedule.setStatus(checkStatus(request.getStartDate(), request.getEndDate()));
        injectionSchedule = injectionScheduleRepository.save(injectionSchedule);
        updateVaccineDate(injectionSchedule);
        return true;
    }

    @Override
    @Transactional(rollbackFor = SQLException.class)
    public boolean update(InjectionScheduleUpdateRequest request, String id) {
        if (!injectionScheduleRepository.existsById(id)) {
            throw new NotFoundException(Message.MSG_171, id);
        }
        InjectionSchedule injectionSchedule = modelMapper.map(request, InjectionSchedule.class);
        injectionSchedule.setId(id);
        injectionSchedule.setStatus(checkStatus(request.getStartDate(), request.getEndDate()));
        injectionSchedule = injectionScheduleRepository.save(injectionSchedule);
        updateVaccineDate(injectionSchedule);
        return true;
    }

    @Override
    public PageDto<InjectionScheduleDto> get(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<InjectionSchedule> injectionSchedulePage = injectionScheduleRepository
                .findByVaccineNameContainsIgnoreCase(search, pageable);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return new PageDto<>(
                page,
                size,
                injectionSchedulePage.getTotalPages(),
                injectionSchedulePage.getTotalElements(),
                injectionSchedulePage.get().map(injectionSchedule -> {
                    var dto = modelMapper.map(injectionSchedule, InjectionScheduleDto.class);
                    dto.setVaccineName(injectionSchedule.getVaccine().getName());
                    dto.setTime(String.format("From %s to %s", injectionSchedule.getStartDate().format(dtf), injectionSchedule.getEndDate().format(dtf)));
                    return dto;
                }).toList()
        );
    }

    @Override
    public InjectionScheduleDetailDto getById(String id) {
        if (!injectionScheduleRepository.existsById(id)) {
            throw new NotFoundException(Message.MSG_171, id);
        }
        InjectionSchedule injectionSchedule = injectionScheduleRepository.findById(id).get();
        InjectionScheduleDetailDto dto = modelMapper.map(injectionSchedule, InjectionScheduleDetailDto.class);
        dto.setVaccineId(injectionSchedule.getVaccine().getId());
        return dto;
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

    @Override
    public void deleteByIds(String ids) {
        String[] idList = ids.split(",");
        List<InjectionSchedule> injectionSchedules = injectionScheduleRepository.findAllById(Arrays.asList(idList));
        for (InjectionSchedule injectionSchedule : injectionSchedules) {
            injectionSchedule.setDeleted(true);
        }
        injectionScheduleRepository.saveAll(injectionSchedules);
    }

    @Override
    public PageDto<InjectionScheduleDto> getInjectionSchedules(int page, int size, InjectionResultAddRequest request) {
        Page<InjectionSchedule> injectionResultPage = injectionScheduleRepository
                .findByStartDateLessThanEqualAndEndDateGreaterThanEqualAndVaccineVaccineTypeId(request.getInjectionDate(), request.getInjectionDate(), request.getVaccineTypeId(), PageRequest.of(page - 1, size));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return new PageDto<>(
                page,
                size,
                injectionResultPage.getTotalPages(),
                injectionResultPage.getTotalElements(),
                injectionResultPage.map((element) -> {
                    var dto = modelMapper.map(element, InjectionScheduleDto.class);
                    dto.setTime(String.format("From %s to %s", element.getStartDate().format(dateTimeFormatter), element.getEndDate().format(dateTimeFormatter)));
                    return dto;
                }).toList()
        );

    }


    private void updateVaccineDate(InjectionSchedule injectionSchedule) {
        Vaccine vaccine = vaccineRepository.findById(injectionSchedule.getVaccine().getId()).orElse(null);
        if (vaccine != null && InjectionScheduleStatus.NOT_YET.equals(injectionSchedule.getStatus()) && vaccine.getTimeBeginNextInjection().isAfter(injectionSchedule.getStartDate())) {
            vaccine.setTimeBeginNextInjection(injectionSchedule.getStartDate());
            vaccine.setTimeEndNextInjection(injectionSchedule.getEndDate());
            vaccineRepository.save(vaccine);
        }

    }
}
