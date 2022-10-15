package com.mouken.modules.zone.db;

import com.mouken.modules.zone.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Zone findByCityAndCountry(String cityName, String countryName);
}