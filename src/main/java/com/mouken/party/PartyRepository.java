package com.mouken.party;

import com.mouken.domain.Party;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface PartyRepository extends JpaRepository<Party, Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "Party.withAll", type = EntityGraph.EntityGraphType.LOAD)
    Party findByPath(String path);

    @EntityGraph(value = "Party.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Party findPartyWithTagsByPath(String path);

    @EntityGraph(value = "Party.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Party findPartyWithZonesByPath(String path);

    @EntityGraph(value = "Party.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Party findPartyWithManagersByPath(String path);

    @EntityGraph(value = "Party.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Party findPartyWithMembersByPath(String path);

    Party findPartyOnlyByPath(String path);
}
