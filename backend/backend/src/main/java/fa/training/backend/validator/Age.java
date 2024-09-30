package fa.training.backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AgeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Age {
    String message() default "Invalid age, must be between {min} and {max} years old";

    int min() default 18;

    int max() default 100;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
