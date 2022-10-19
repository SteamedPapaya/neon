package com.mouken.modules.account.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UsernameForm {

    @NotBlank
    @Length(min = 3, max = 20)
    @Pattern(regexp = "^[a-z0-9_-]{3,20}$")
    private String newUsername;

}
