package com.mouken.party;

import com.mouken.account.CurrentAccount;
import com.mouken.domain.Account;
import com.mouken.domain.Party;
import com.mouken.party.form.PartyDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/party/{path}/settings")
@RequiredArgsConstructor
public class PartySettingsController {

    private final PartyService partyService;
    private final ModelMapper modelMapper;

    @GetMapping("/description")
    public String viewPartySetting(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getPartyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(party);
        model.addAttribute(modelMapper.map(party, PartyDescriptionForm.class));
        return "party/settings/description";
    }

    @PostMapping("/description")
    public String updatePartyInfo(@CurrentAccount Account account, @PathVariable String path,
                                  @Valid PartyDescriptionForm partyDescriptionForm, Errors errors,
                                  Model model, RedirectAttributes attributes) {
        Party party = partyService.getPartyToUpdate(account, path);

        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(party);
            return "party/settings/description";
        }

        partyService.updatePartyDescription(party, partyDescriptionForm);
        attributes.addFlashAttribute("message", "Party info is updated.");
        return "redirect:/party/" + getPath(path) + "/settings/description";
    }

    private String getPath(String path) {
        return URLEncoder.encode(path, StandardCharsets.UTF_8);
    }
}