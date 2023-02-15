package com.mouken.modules.account.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.List;

@Data
public class AccountModifyForm {

    private Long id;

    @Pattern(regexp = "^[a-z0-9_-]{3,20}$")
    private String username;

    @Email
    @NotBlank
    private String email;

    @Nullable
    private List<String> roleNameList;
}