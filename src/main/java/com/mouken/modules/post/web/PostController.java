package com.mouken.modules.post.web;

import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.party.service.PartyService;
import com.mouken.modules.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ModelMapper modelMapper;
    private final PartyService partyService;

    //todo
    @GetMapping("/new-post")
    public String newPostForm(@CurrentAccount Account account, Model model) {
        return "post/form";
    }

    //todo
/*    @PostMapping("new-post")
    public String newPostSubmit(@CurrentAccount Account account, @Validated PostForm postForm, BindingResult errors, Model model) {

        Party party = partyService.getPartyToPost(account, );
        if (errors.hasErrors()) {

            return "post/form";
        }

        Post post = postService.createPost(modelMapper.map(postForm, Post.class), party, account);
        return "redirect:/party/" + party.getPath() + "/post/" + post.getPath();
    }*/
}