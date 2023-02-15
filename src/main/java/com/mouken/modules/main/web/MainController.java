package com.mouken.modules.main.web;

import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.Account;
import com.mouken.modules.party.event.db.EnrollmentRepository;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.service.PartyService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

/*    private final ModelMapper modelMapper;
    private final PartyRepository partyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;
    private final PartyService partyService;
    private final PostService postService;
    private final PostRepository postRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        model.addAttribute("account", account);
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createdDateTime"));
        model.addAttribute("postList", postRepository.findSliceBy(pageRequest));
        model.addAttribute("partyList", partyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));
        return "index";
    }

    @GetMapping("/dashboard")
    public String dashboard(@CurrentAccount Account account, Model model) {

        Account accountLoaded = accountRepository.findAccountWithTagsAndZonesById(account.getId());
        model.addAttribute(accountLoaded);
        model.addAttribute("enrollmentList", enrollmentRepository.findByAccountAndAcceptedOrderByEnrolledAtDesc(accountLoaded, true));
        model.addAttribute("partyList", partyRepository.findByAccount(accountLoaded.getTags(), accountLoaded.getZones()));
        model.addAttribute("partyManagerOf", partyRepository.findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
        model.addAttribute("partyMemberOf", partyRepository.findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(account, false));
        return "dashboard";
    }

    *//* todo delete @GetMapping("/login")
    public String loginForm() {
        return "login";
    }*//*

    @GetMapping("/search/party")
    public String searchParty(
            @PageableDefault(size = 9, sort = "publishedDateTime", direction = Sort.Direction.DESC) Pageable pageable,
            String keyword,
            Model model) {

        Page<Party> partyPage = partyRepository.findByKeyword(keyword, pageable);
        model.addAttribute("partyPage", partyPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortProperty", pageable.getSort().toString().contains("publishedDateTime") ? "publishedDateTime" : "memberCount");
        return "search";
    }

    @GetMapping("/new-post")
    public String newPostForm(@CurrentAccount Account account, Model model) {

        model.addAttribute(account);
        model.addAttribute(new PostForm());
        return "main/post-form";
    }

    @PostMapping("/new-post")
    public String newPostSubmit(@CurrentAccount Account account, @Validated PostForm postForm, BindingResult bindingResult, Model model) {

        Party party = partyService.getPartyToUpdateStatus(account, postForm.getPartyPath());

        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(party);
            return "main/post-form";
        }

        Post post = postService.createPost(modelMapper.map(postForm, Post.class), party, account);
        return "redirect:/party/" + party.getPath() + "/posts/" + post.getPath();
    }*/
}
