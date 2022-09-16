package com.mouken.settings.form;

import com.mouken.domain.Zone;
import lombok.Data;

@Data
public class ZoneForm {

    private String zoneName;

    public String getCityName() {
        return zoneName.substring(0, zoneName.indexOf("/"));
    }

    public String getProvinceName() {
        return zoneName.substring(zoneName.indexOf("/") + 1, zoneName.indexOf("("));
    }

    public String getCountryName() {
        return zoneName.substring(zoneName.indexOf("(") + 1, zoneName.indexOf(")"));
    }

    public Zone getZone() { // parsing
        return Zone.builder().city(this.getCityName())
                .province(this.getProvinceName())
                .country(this.getCountryName()).build();
    }

}