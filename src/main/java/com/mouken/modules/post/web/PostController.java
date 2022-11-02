package com.mouken.modules.post.web;

import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.service.PartyService;
import com.mouken.modules.post.domain.Post;
import com.mouken.modules.post.service.PostService;
import com.mouken.modules.post.web.form.PostForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/party/{path}")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PartyService partyService;
    private final ModelMapper modelMapper;

    // todo
    @GetMapping("/posts/{id}")
    public String getPost() {

        return "post/view";
    }
}