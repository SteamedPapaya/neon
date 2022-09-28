package com.mouken.modules.party;

import com.mouken.modules.tag.Tag;
import com.mouken.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface PartyRepositoryExtension {

    Page<Party> findByKeyword(String keyword, Pageable pageable);

    List<Party> findByAccount(Set<Tag> tags, Set<Zone> zones);

}