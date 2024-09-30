package fa.training.backend.controller;

import fa.training.backend.dto.request.employee.EmployeeRequest;
import fa.training.backend.dto.request.employee.EmployeeUpdateDto;
import fa.training.backend.dto.response.employee.EmployeeListDto;
import fa.training.backend.dto.response.PageDto;
import fa.training.backend.dto.response.Response;
import fa.training.backend.service.EmployeeService;
import fa.training.backend.util.Message;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ResponseEntity<Response<PageDto<EmployeeListDto>>> getAllEmployeeDto(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false, defaultValue = "") String search) {

        PageDto<EmployeeListDto> data = employeeService.getAllEmployeesByNameContains(PageRequest.of(page - 1, size), search);

        Response<PageDto<EmployeeListDto>> response = new Response<>(
                Message.MSG_261.getCode(),
                Message.MSG_261.getMessage(),
                data
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<EmployeeUpdateDto>> getEmployeeById(@PathVariable String id) {
        EmployeeUpdateDto data = employeeService.getEmployeeById(id);
        Response<EmployeeUpdateDto> response = new Response<>(
                Message.MSG_262.getCode(),
                Message.MSG_262.getMessage(),
                data
        );

        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<Response<Boolean>> deleteEmployeesByIds(@RequestParam String ids) {
        employeeService.deleteEmployeesByIds(ids);

        Response<Boolean> response = new Response<>(
                Message.MSG_265.getCode(),
                Message.MSG_265.getMessage(),
                true
        );

        return ResponseEntity.ok(response);
    }


    @PostMapping
    public ResponseEntity<Response<Boolean>> postEmployee(@Valid @RequestBody EmployeeRequest employeeRequestDto) {
        employeeService.createEmployee(employeeRequestDto);
        Response<Boolean> response = new Response<>(
                Message.MSG_263.getCode(),
                Message.MSG_263.getMessage(),
                true
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Response<Boolean>> updateEmployee(
            @PathVariable String id,
            @Valid @RequestBody EmployeeUpdateDto employeeUpdateDto)
    {
        employeeService.updateEmployee(employeeUpdateDto, id);
        Response<Boolean> response = new Response<>(
                Message.MSG_264.getCode(),
                Message.MSG_264.getMessage(),
                true
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<Response<Boolean>> updateAvatarEmployee(
            MultipartFile image,
            @PathVariable String id
    ) throws IOException
    {
        employeeService.updateAvatar(image, id);
        Response<Boolean> response = new Response<>(
                Message.MSG_264.getCode(),
                Message.MSG_264.getMessage(),
                true
        );
        return ResponseEntity.ok(response);
    }
}
