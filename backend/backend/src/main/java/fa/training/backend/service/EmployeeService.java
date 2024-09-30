package fa.training.backend.service;

import fa.training.backend.dto.request.employee.EmployeeRequest;
import fa.training.backend.dto.request.employee.EmployeeUpdateDto;
import fa.training.backend.dto.response.employee.EmployeeListDto;
import fa.training.backend.dto.response.PageDto;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface EmployeeService {

    PageDto<EmployeeListDto> getAllEmployeesByNameContains(Pageable pageable, String search);

    EmployeeUpdateDto getEmployeeById(String id);

    boolean deleteEmployeesByIds(String ids);

    boolean createEmployee(EmployeeRequest employeeRequest);

    boolean updateEmployee(EmployeeUpdateDto employeeUpdateDto, String id);

    boolean updateAvatar(MultipartFile image, String id) throws IOException;
} 
