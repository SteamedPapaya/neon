package com.mouken.modules.party.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class PartyForm {

/*    @NotBlank
    @Length(min = 2, max = 30)
    @Pattern(regexp = "^[a-z0-9_-]{2,20}$")*/
    private String path;

    @NotBlank
    @Length(max = 50)
    private String title;

    @NotBlank
    @Length(max = 100)
    private String shortDescription;

    @NotBlank
    private String fullDescription;

}
