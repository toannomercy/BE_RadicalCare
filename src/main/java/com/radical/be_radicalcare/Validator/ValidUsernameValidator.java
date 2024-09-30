package com.radical.be_radicalcare.Validator;

import com.radical.be_radicalcare.Validator.Annotation.ValidUsername;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUsernameValidator implements ConstraintValidator<ValidUsername, String> {
    public ValidUsernameValidator() {
    }

    @Override
    public void initialize(ValidUsername constraintAnnotation) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && !username.isEmpty() && username.matches("[a-zA-Z0-9_]+");
    }
}
