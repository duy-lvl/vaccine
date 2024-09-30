package fa.training.backend.service.impl;

import fa.training.backend.dto.request.employee.EmployeeRequest;
import fa.training.backend.dto.request.employee.EmployeeUpdateDto;
import fa.training.backend.dto.response.employee.EmployeeListDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.exception.common.*;
import fa.training.backend.model.Employee;
import fa.training.backend.repository.EmployeeRepository;
import fa.training.backend.service.EmployeeService;
import fa.training.backend.service.FirebaseService;
import fa.training.backend.util.Message;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final FirebaseService firebaseService;
    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, FirebaseService firebaseService, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.employeeRepository = employeeRepository;
        this.firebaseService = firebaseService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public PageDto<EmployeeListDto> getAllEmployeesByNameContains(Pageable pageable, String search) {
        var page = employeeRepository.findByNameContainsIgnoreCaseOrIdContainsIgnoreCase(pageable, search);
        PageDto<EmployeeListDto> pageDto = new PageDto<>();
        List<EmployeeListDto> employeeDtoList = page.getContent().stream()
                .map(employee -> {
                    var dto = modelMapper.map(employee, EmployeeListDto.class);
                    try {
                        dto.setImage(firebaseService.getFileUrl(employee.getImage()));
                    } catch (Exception e) {
                        dto.setImage(null);
                    }
                    return dto;
                })
                .toList();
        pageDto.setData(employeeDtoList);
        pageDto.setPage(pageable.getPageNumber() + 1);
        pageDto.setSize(pageable.getPageSize());
        pageDto.setTotalPages(page.getTotalPages());
        pageDto.setTotalElements(page.getTotalElements());
        return pageDto;
    }

    @Override
    public EmployeeUpdateDto getEmployeeById(String id) {
        EmployeeUpdateDto employeeDto;

        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee == null) {
            throw new NotFoundException(Message.MSG_161, id);
        }
        employeeDto = modelMapper.map(employee, EmployeeUpdateDto.class);
        try {
            employeeDto.setUrlImage(firebaseService.getFileUrl(employee.getImage()));
        } catch (Exception e) {
            employeeDto.setImage(null);
            employeeDto.setUrlImage(null);
        }
        return employeeDto;
    }

    @Override
    public boolean deleteEmployeesByIds(String ids) {
        String[] idList = ids.split(",");
        List<Employee> employees = employeeRepository.findAllById(Arrays.asList(idList));
        for (Employee employee : employees) {
            employee.setDeleted(true);
        }
        employeeRepository.saveAll(employees);
        return true;
    }

    @Override
    public boolean createEmployee(EmployeeRequest employeeRequest) {
        Employee employee = modelMapper.map(employeeRequest, Employee.class);

        if (employeeRepository.existsByPhone(employeeRequest.getPhone())) {
            throw new PhoneDuplicateException(Message.MSG_162);
        }
        if (employeeRepository.existsByEmail(employeeRequest.getEmail())) {
            throw new EmailDuplicateException(Message.MSG_164);
        }
        if (employeeRepository.existsByUsername(employeeRequest.getUsername())) {
            throw new UsernameDuplicateException(Message.MSG_163);
        }


        employee.setPassword(passwordEncoder.encode(employeeRequest.getPassword()));
        employeeRepository.save(employee);
        return true;
    }
    @Override
    public boolean updateEmployee(EmployeeUpdateDto employeeUpdateDto, String id) {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_161, id);
        }
        Employee employee = employeeOptional.get();
        if (!employee.getPhone().equals(employeeUpdateDto.getPhone()) &&
                employeeRepository.existsByPhone(employeeUpdateDto.getPhone())) {
            throw new PhoneDuplicateException(Message.MSG_162);
        }
        if (!employee.getEmail().equals(employeeUpdateDto.getEmail()) &&
                employeeRepository.existsByEmail(employeeUpdateDto.getEmail())) {
            throw new EmailDuplicateException(Message.MSG_164);
        }
        if (!employee.getUsername().equals(employeeUpdateDto.getUsername()) &&
                employeeRepository.existsByUsername(employeeUpdateDto.getUsername())) {
            throw new UsernameDuplicateException(Message.MSG_163);
        }



        modelMapper.map(employeeUpdateDto, employee);

        employeeRepository.save(employee);
        return true;
    }

    @Override
    public boolean updateAvatar(MultipartFile image, String id) throws IOException {
        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isEmpty()) {
            throw new NotFoundException(Message.MSG_161, id);
        }
        Employee employee = employeeOptional.get();

        String imageName = firebaseService.upload(image);
        employee.setImage(imageName);
        employeeRepository.save(employee);
        return true;
    }
}
