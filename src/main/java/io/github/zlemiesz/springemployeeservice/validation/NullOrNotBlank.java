package io.github.zlemiesz.springemployeeservice.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * @author Zbigniew Lemiesz
 */



@Documented
@Constraint(validatedBy = NullOrNotBlankValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NullOrNotBlank {
    String message() default "{common.notBlank}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
