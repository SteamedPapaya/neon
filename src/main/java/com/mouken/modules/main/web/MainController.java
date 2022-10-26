package com.mouken.modules.main.web;

import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.db.AccountRepository;
import com.mouken.modules.account.domain.Account;
import com.mouken.modules.event.db.EnrollmentRepository;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MainController {

    private final PartyRepository partyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        model.addAttribute("account", account);
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

    @GetMapping("/login")
    public String loginForm() {
        return "login";
    }

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
}
