package com.mouken.modules.party;

import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.Account;
import com.mouken.modules.party.form.PartyForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final ModelMapper modelMapper;
    private final PartyRepository partyRepository;

    @GetMapping("/party/{path}/members")
    public String viewPartyMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getParty(path);
        model.addAttribute(account);
        model.addAttribute(party);
        return "party/members";
    }
    
    @GetMapping("/party/{path}")
    public String viewParty(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getParty(path);
        model.addAttribute(account);
        model.addAttribute(party);
        return "party/view";
    }
    
    @GetMapping("/new-party")
    public String newPartyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PartyForm());
        return "party/form";
    }

    @PostMapping("/new-party")
    public String newPartySubmit(
            @CurrentAccount Account account,
            @Valid PartyForm partyForm,
            Errors errors,
            Model model) {

        partyForm.setPath(partyService.getNewPath());
        log.info("PARTY PATH = {}", partyForm.getPath());
        if (errors.hasErrors()) {
            log.info("ERROR={}", errors.toString());
            model.addAttribute(account);
            return "party/form";
        }

        Party newParty = partyService.createNewParty(modelMapper.map(partyForm, Party.class), account);
        log.info("PATH ENCODED = {}", URLEncoder.encode(newParty.getPath(), StandardCharsets.UTF_8));
        return "redirect:/party/" + URLEncoder.encode(newParty.getPath(), StandardCharsets.UTF_8);
    }


    @PostMapping("/party/{path}/join")
    public String joinParty(@CurrentAccount Account account, @PathVariable String path) {
        Party party = partyRepository.findPartyWithMembersByPath(path);
        partyService.addMember(party, account);
        return "redirect:/party/" + party.getPath() + "/members";
    }

    @PostMapping("/party/{path}/leave")
    public String leaveParty(@CurrentAccount Account account, @PathVariable String path) {
        Party party = partyRepository.findPartyWithMembersByPath(path);
        partyService.removeMember(party, account);
        return "redirect:/party/" + party.getPath() + "/members";
    }
    
}