package com.mouken.modules.resource.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ResourceModifyForm {

    private Long id;
    @NotNull
    private String name;
    private String httpMethod;
    private int orderNum;
    private String type;
    private List<String> roleNameList;

}
