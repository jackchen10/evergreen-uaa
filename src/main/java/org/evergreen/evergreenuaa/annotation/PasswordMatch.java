package org.evergreen.evergreenuaa.annotation;

import org.evergreen.evergreenuaa.validation.PasswordConstraintValidator;
import org.evergreen.evergreenuaa.validation.PasswordMatchValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordMatchValidator.class)
@Documented
public @interface PasswordMatch {
    String message() default "Password Not Matched";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
