package com.mouken.modules.party.web;

import com.mouken.modules.account.CurrentAccount;
import com.mouken.modules.account.Account;
import com.mouken.modules.party.db.PartyRepository;
import com.mouken.modules.party.domain.Party;
import com.mouken.modules.party.service.PartyService;
import com.mouken.modules.party.validator.PartyFormValidator;
import com.mouken.modules.party.web.form.PartyForm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@Controller
@RequiredArgsConstructor
public class PartyController {

    private final PartyService partyService;
    private final ModelMapper modelMapper;
    private final PartyRepository partyRepository;
    private final PartyFormValidator partyFormValidator;

    @InitBinder("partyForm")
    public void partyFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(partyFormValidator);
    }

    @GetMapping("/party/{path}")
    public String viewParty(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getParty(path);
        model.addAttribute(account);
        model.addAttribute(party);
        return "party/view";
    }

    @GetMapping("/party/{path}/members")
    public String viewPartyMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getParty(path);
        model.addAttribute(account);
        model.addAttribute(party);
        return "party/members";
    }
    
    @GetMapping("/party/{path}/info")
    public String viewPartyInfo(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getParty(path);
        model.addAttribute(account);
        model.addAttribute(party);
        return "party/info";
    }
    
    @GetMapping("/new-party")
    public String newPartyForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PartyForm());
        return "party/form";
    }

    @PostMapping("/new-party")
    public String newPartySubmit(@CurrentAccount Account account, @Validated PartyForm partyForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute(account);
            return "party/form";
        }

        Party newParty = partyService.createNewParty(modelMapper.map(partyForm, Party.class), account);

        // set default party banner
        int max_num = 24;
        String random_num = String.valueOf((int) (Math.random() * max_num) + 1);
        String image_path = "/assets/banner/" + random_num + ".png";
        partyService.updatePartyBanner(newParty, image_path);

        return "redirect:/party/" + URLEncoder.encode(newParty.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/party/{path}/join")
    public String joinParty(@CurrentAccount Account account, @PathVariable String path) {
        Party party = partyRepository.findPartyWithMembersByPath(path);
        partyService.addMember(party, account);
        return "redirect:/party/" + party.getPath() + "/members";
    }

    @GetMapping("/party/{path}/leave")
    public String leaveParty(@CurrentAccount Account account, @PathVariable String path) {
        Party party = partyRepository.findPartyWithMembersByPath(path);
        partyService.removeMember(party, account);
        return "redirect:/party/" + party.getPath() + "/members";
    }
    
}