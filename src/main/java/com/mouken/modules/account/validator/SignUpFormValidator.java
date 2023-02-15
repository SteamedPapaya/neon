package com.mouken.modules.account.validator;

import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.web.form.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class SignUpFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> aClass) {
        return SignUpForm.class.isAssignableFrom(SignUpForm.class);
    }

    @Override
    public void validate(Object o, Errors errors) {

        SignUpForm signUpForm = (SignUpForm) o;

        if (accountRepository.existsByEmail(signUpForm.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{signUpForm.getEmail()}, "an invalid email");
        }
        if (accountRepository.existsByUsername(signUpForm.getUsername())) {
            errors.rejectValue("username", "invalid.username", new Object[]{signUpForm.getUsername()}, "an invalid ID");
        }
    }
}
