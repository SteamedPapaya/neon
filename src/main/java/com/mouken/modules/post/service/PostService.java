package com.mouken.modules.post.service;

import com.mouken.modules.account.domain.Account;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.event.PartyUpdateEvent;
import com.mouken.modules.post.db.PostRepository;
import com.mouken.modules.post.domain.Post;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class PostService {

    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final PostRepository postRepository;

    public Post createPost(Post post, Party party, Account account) {
        post.setCreatedBy(account);
        post.setCreatedDateTime(LocalDateTime.now());
        post.setParty(party);
        eventPublisher.publishEvent(new PartyUpdateEvent(post.getParty(), "new post " + post.getId()));
        return postRepository.save(post);
    }
}
