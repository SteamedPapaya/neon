package com.mouken.modules.party.validator;

import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.web.form.PartyForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@RequiredArgsConstructor
public class PartyFormValidator implements Validator {
    private final PartyRepository partyRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return PartyForm.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PartyForm partyForm = (PartyForm)target;
        if (partyRepository.existsByPath(partyForm.getPath())) {
            errors.rejectValue("path", "exist.path", "The path already exist.");
        }
    }
}
