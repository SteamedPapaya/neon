package com.mouken.modules.post.web.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Data
public class PostForm {

    @NotBlank
    @Length(max = 50)
    private String title;

    private String description;
}
