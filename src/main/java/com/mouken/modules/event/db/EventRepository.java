package com.mouken.modules.event.db;

import com.mouken.modules.event.domain.Event;
import com.mouken.modules.party.domain.Party;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = "enrollments", type = EntityGraph.EntityGraphType.LOAD)
    List<Event> findByPartyOrderByStartDateTime(Party party);
}
