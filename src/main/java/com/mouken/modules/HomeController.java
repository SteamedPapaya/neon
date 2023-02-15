package com.mouken.modules;

import com.mouken.modules.account.Account;
import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.PrincipalUser;
import com.mouken.modules.account.ProviderUser;
import com.mouken.modules.account.repository.AccountRepository;
import com.mouken.modules.account.service.AccountService;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.event.db.EnrollmentRepository;
import com.mouken.modules.party.service.PartyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.mouken.modules.util.SecurityUrl.ACCESS_DENIED_URL;
import static com.mouken.modules.util.SecurityUrl.ACCESS_DENIED_VIEW_NAME;


@Slf4j
@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final PartyRepository partyRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AccountRepository accountRepository;
    private final PartyService partyService;

    @GetMapping("/")
    public String index(@AuthenticationPrincipal PrincipalUser principalUser, Model model) {

        if (principalUser != null) {
            ProviderUser providerUser = principalUser.getProviderUser();
            model.addAttribute("username", providerUser.getUsername());
            model.addAttribute("account", accountService.getAccount(providerUser.getUsername())); // todo ac-1
        }
        model.addAttribute("partyList", partyRepository.findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(true, false));

        return "index";
    }

    @GetMapping("/api/user")
    public Authentication user(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
        log.info("authentication = {1}\noAuth2User ={2}", authentication, oAuth2User);
        return authentication;
    }

    // 구글과 달리 네이버는 OIDC가 안되므로 이를 비교하기위해 구분
    @GetMapping("/api/oidc")
    public Authentication oidc(Authentication authentication, @AuthenticationPrincipal OidcUser oidcUser) {
        log.info("authentication = {1}\noidcUser ={2}", authentication, oidcUser);
        return authentication;
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

    @GetMapping(ACCESS_DENIED_URL)
    public String denied(
            @CurrentAccount Account account, // todo test
            @RequestParam(value = "exception", required = false) String exception,
            Model model) {
        model.addAttribute("username", account.getUsername()); // todo test
        model.addAttribute("exception", exception);
        return ACCESS_DENIED_VIEW_NAME;
    }

    @GetMapping("/test")
    public String test() {
        return "test";
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
