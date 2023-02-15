package com.mouken.modules.role.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleCreateForm {

    // todo unique
    @NotBlank
    private String name;

    private String description;
}
