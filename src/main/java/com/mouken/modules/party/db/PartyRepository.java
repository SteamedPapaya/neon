package com.mouken.modules.party.db;

import com.mouken.modules.account.Account;
import com.mouken.modules.party.PartyRepositoryExtension;
import com.mouken.modules.party.domain.Party;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PartyRepository extends JpaRepository<Party, Long>, PartyRepositoryExtension {

    boolean existsByPath(String path);

    List<Party> findAll();

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Party findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Party findPartyWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Party findPartyWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Party findPartyWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Party findPartyWithMembersByPath(String path);

    Party findPartyOnlyByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    Party findPartyWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Party findPartyWithManagersAndMembersById(Long id);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Party> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Party> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Party> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

}
