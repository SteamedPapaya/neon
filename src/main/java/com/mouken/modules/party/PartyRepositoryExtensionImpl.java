package com.mouken.modules.party;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.mouken.modules.account.QAccount;
import com.mouken.modules.tag.QTag;
import com.mouken.modules.tag.Tag;
import com.mouken.modules.zone.QZone;
import com.mouken.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class PartyRepositoryExtensionImpl extends QuerydslRepositorySupport implements PartyRepositoryExtension {

    public PartyRepositoryExtensionImpl() {
        super(Party.class);
    }

    @Override
    public Page<Party> findByKeyword(String keyword, Pageable pageable) {
        QParty party = QParty.party;
        JPQLQuery<Party> query = from(party).where(party.published.isTrue()
                        .and(party.title.containsIgnoreCase(keyword))
                        .or(party.tags.any().title.containsIgnoreCase(keyword))
                        .or(party.zones.any().city.containsIgnoreCase(keyword)))
                .leftJoin(party.tags, QTag.tag).fetchJoin()
                .leftJoin(party.zones, QZone.zone).fetchJoin()
                .distinct();
        JPQLQuery<Party> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Party> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Party> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QParty party = QParty.party;
        JPQLQuery<Party> query = from(party).where(party.published.isTrue()
                        .and(party.closed.isFalse())
                        .and(party.tags.any().in(tags))
                        .and(party.zones.any().in(zones)))
                .leftJoin(party.tags, QTag.tag).fetchJoin()
                .leftJoin(party.zones, QZone.zone).fetchJoin()
                .orderBy(party.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }
}