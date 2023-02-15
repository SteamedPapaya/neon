package com.mouken.modules.security.initializer;

import com.mouken.modules.security.service.SecurityResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SecurityInitializer implements ApplicationRunner {

    private final SecurityResourceService securityResourceService;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        securityResourceService.setRoleHierarchy();
    }
}