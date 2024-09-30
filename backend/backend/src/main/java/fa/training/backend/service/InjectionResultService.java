package fa.training.backend.service;

import fa.training.backend.dto.request.injection_result.InjectionResultRequest;
import fa.training.backend.dto.response.injection_result.InjectionResultDetailDto;
import fa.training.backend.dto.response.injection_result.InjectionResultDto;
import fa.training.backend.dto.response.PageDto;

public interface InjectionResultService {
    boolean add(InjectionResultRequest request);
    boolean update(InjectionResultRequest request, String id);
    PageDto<InjectionResultDto> getAll(int page, int size, String search);
    InjectionResultDetailDto getById(String id);
    void deleteByIds(String ids);


}
