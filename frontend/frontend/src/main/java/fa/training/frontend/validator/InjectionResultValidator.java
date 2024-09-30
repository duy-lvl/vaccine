package fa.training.frontend.validator;

import fa.training.frontend.dto.request.injection_reasult.InjectionResultRequest;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class InjectionResultValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return InjectionResultRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var request = (InjectionResultRequest) target;




//        if (request.getInjectionDate().isBefore(LocalDate.now())) {
//            errors.rejectValue("injectionDate", "error.injectionDate", "Please input Date of injection with value greater or equal the current date");
//        }
    }
}
