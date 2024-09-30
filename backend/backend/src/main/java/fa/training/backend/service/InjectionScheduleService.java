package fa.training.backend.service;

import fa.training.backend.dto.request.injection_schedule.InjectionScheduleAddRequest;
import fa.training.backend.dto.request.injection_schedule.InjectionScheduleUpdateRequest;
import fa.training.backend.dto.request.injection_result.InjectionResultAddRequest;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDetailDto;
import fa.training.backend.dto.response.injection_schedule.InjectionScheduleDto;
import fa.training.backend.dto.response.PageDto;

public interface InjectionScheduleService {
    boolean add(InjectionScheduleAddRequest request);
    boolean update(InjectionScheduleUpdateRequest request, String id);
    PageDto<InjectionScheduleDto> get(int page, int size, String search);
    InjectionScheduleDetailDto getById(String id);
    void deleteByIds(String ids);
     PageDto<InjectionScheduleDto> getInjectionSchedules(int page, int size, InjectionResultAddRequest request);
}
