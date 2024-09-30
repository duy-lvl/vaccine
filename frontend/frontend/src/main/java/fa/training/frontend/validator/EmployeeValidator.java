package fa.training.frontend.validator;

import fa.training.frontend.dto.request.employee.EmployeeRequest;
import fa.training.frontend.dto.request.employee.EmployeeUpdateDto;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

@Component
public class EmployeeValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return EmployeeRequest.class.equals(clazz) || EmployeeUpdateDto.class.equals(clazz); // Support both classes
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        if (target instanceof EmployeeRequest employeeRequest) {
            validateEmployeeRequest(employeeRequest, errors);
        } else if (target instanceof EmployeeUpdateDto employeeUpdateDto) {
            validateEmployeeUpdateDto(employeeUpdateDto, errors);
        }
    }

    private void validateEmployeeRequest(@NonNull EmployeeRequest employeeRequest, @NonNull Errors errors) {
        if (Period.between(employeeRequest.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
            errors.rejectValue("dateOfBirth", "error.dateOfBirth", "The age must be greater than 18");
        }
    }

    private void validateEmployeeUpdateDto(@NonNull EmployeeUpdateDto employeeUpdateDto, @NonNull Errors errors) {
        // Add specific validation for EmployeeUpdateDto here if needed
        if (Period.between(employeeUpdateDto.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
            errors.rejectValue("dateOfBirth", "error.dateOfBirth", "The age must be greater than 18");
        }
    }
}
