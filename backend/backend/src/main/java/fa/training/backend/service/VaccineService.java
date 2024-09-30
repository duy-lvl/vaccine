package fa.training.backend.service;

import fa.training.backend.dto.request.vaccine.VaccineAddRequest;
import fa.training.backend.dto.request.vaccine.VaccineInjectionScheduleRequestDto;
import fa.training.backend.dto.request.vaccine.VaccineUpdateRequest;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.vaccine.VaccineIndividualDto;
import fa.training.backend.dto.response.vaccine.VaccineUpdateDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface VaccineService {
    byte[] createTemplate() throws IOException;

    void importVaccine(MultipartFile file);

    boolean add(VaccineAddRequest vaccineAddRequest);

    boolean update(VaccineUpdateRequest vaccineUpdateRequest, String id);

    PageDto<VaccineIndividualDto> getAll(Pageable pageable, String query);

    VaccineUpdateDto getById(String id);

    VaccineUpdateDto getByName(String name);

    boolean switchStatus(String id);

    List<VaccineInjectionScheduleRequestDto> getAllVaccines();

    boolean updateVaccineInactive(String ids);

    void deleteByIds(String ids);
}
