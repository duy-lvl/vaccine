package fa.training.backend.service;

import fa.training.backend.dto.request.vaccine_type.NewVaccineTypeRequest;
import fa.training.backend.dto.request.vaccine_type.UpdateVaccineTypeRequest;
import fa.training.backend.dto.response.*;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeListDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeNameDto;
import fa.training.backend.dto.response.vaccine_type.VaccineTypeUpdateDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VaccineTypeService {
    String add(NewVaccineTypeRequest request);
    VaccineTypeUpdateDto findById(String id);
    PageDto<VaccineTypeListDto> getAll(Pageable pageable);
    boolean update(UpdateVaccineTypeRequest newVaccineTypeRequest, String id);
//    boolean updateVaccineTypeInactive(String ids);
    PageDto<VaccineTypeListDto> searchVaccineTypes(String query, Pageable pageable);
    boolean updateVaccineTypeInactive(List<String> ids);
    boolean updateVaccineTypeActive(List<String> ids);
    void deleteByIds(String ids);
    List<VaccineTypeNameDto> getNames(boolean isActive);

    boolean updateImage(String id, String imageUrl);

}
