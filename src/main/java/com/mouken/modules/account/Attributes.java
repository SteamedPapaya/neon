package com.mouken.modules.account;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class Attributes {

    private Map<String, Object> firstLayerAttributes;
    private Map<String, Object> secondLayerAttributes;
    private Map<String, Object> thirdLayerAttributes;

}
