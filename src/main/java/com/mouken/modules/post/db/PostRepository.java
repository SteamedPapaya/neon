package com.mouken.modules.post.db;

import com.mouken.modules.party.domain.Party;
import com.mouken.modules.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByPartyOrderByCreatedDateTimeDesc(Party party);

}
