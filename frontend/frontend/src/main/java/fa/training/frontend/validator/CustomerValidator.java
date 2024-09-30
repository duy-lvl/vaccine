package fa.training.frontend.validator;

import fa.training.frontend.dto.request.customer.CustomerAddRequestDto;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.time.Period;

@Component
public class CustomerValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerAddRequestDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomerAddRequestDto customerAddRequestDto = (CustomerAddRequestDto) target;

        if (Period.between(customerAddRequestDto.getDateOfBirth(), LocalDate.now()).getYears() < 18) {
            errors.rejectValue("dateOfBirth", "error.dateOfBirth", "The age must be bigger 18");
        }

    }

}
