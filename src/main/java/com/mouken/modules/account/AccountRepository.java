package com.mouken.modules.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true) // TODO readOnly 성능 향상
public interface AccountRepository extends JpaRepository<Account, Long> {
    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    Account findByEmail(String email);

    Account findByUsername(String username);

}
