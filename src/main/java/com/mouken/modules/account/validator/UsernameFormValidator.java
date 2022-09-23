package com.mouken.modules.account.validator;

import com.mouken.modules.account.AccountRepository;
import com.mouken.modules.account.form.UsernameForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class UsernameFormValidator implements Validator {

    private final AccountRepository accountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UsernameForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UsernameForm usernameForm = (UsernameForm) target;

        // is that username already exist?
        boolean existsByUsername = accountRepository.existsByUsername(usernameForm.getNewUsername());
        if (existsByUsername) {
            errors.rejectValue("username", "wrong.value", "You can use it as your username.");
        }
    }
}
