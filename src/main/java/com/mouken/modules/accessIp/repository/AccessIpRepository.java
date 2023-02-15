package com.mouken.modules.accessIp.repository;

import com.mouken.modules.accessIp.AccessIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessIpRepository extends JpaRepository<AccessIp, Long> {
    boolean existsByAddress(String address);
}
