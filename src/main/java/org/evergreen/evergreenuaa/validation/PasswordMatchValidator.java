package org.evergreen.evergreenuaa.validation;

import lombok.val;
import org.evergreen.evergreenuaa.annotation.PasswordMatch;
import org.evergreen.evergreenuaa.annotation.ValidPassword;
import org.evergreen.evergreenuaa.entity.dto.UserDto;
import org.passay.PasswordData;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator  implements ConstraintValidator<PasswordMatch, UserDto> {
    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final UserDto userDto, final ConstraintValidatorContext constraintValidatorContext) {
        return userDto.getPassword().equals(userDto.getMatchPassword());
    }
}
