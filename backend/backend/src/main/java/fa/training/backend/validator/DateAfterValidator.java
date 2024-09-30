package fa.training.backend.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.beanutils.BeanUtils;

import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateAfter, Object> {
    private String startDate;
    private String endDate;
    private String message;

    @Override
    public void initialize(final DateAfter constraintAnnotation) {
        startDate = constraintAnnotation.startDate();
        endDate = constraintAnnotation.endDate();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        boolean valid = true;
        try {
            final LocalDate firstObj = LocalDate.parse(BeanUtils.getProperty(value, startDate));
            final LocalDate secondObj = LocalDate.parse(BeanUtils.getProperty(value, endDate));
            valid = secondObj.isAfter(firstObj);
        } catch (final Exception ignore) {
            // ignore
        }

        if (!valid) {
            context.buildConstraintViolationWithTemplate(message)
                    .addPropertyNode(startDate)
                    .addConstraintViolation()
                    .disableDefaultConstraintViolation();
        }

        return valid;
    }
}
