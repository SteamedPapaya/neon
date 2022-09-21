package com.mouken.party;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouken.account.CurrentAccount;
import com.mouken.domain.Account;
import com.mouken.domain.Party;
import com.mouken.domain.Tag;
import com.mouken.domain.Zone;
import com.mouken.party.form.PartyDescriptionForm;
import com.mouken.settings.form.TagForm;
import com.mouken.settings.form.ZoneForm;
import com.mouken.tag.TagRepository;
import com.mouken.tag.TagService;
import com.mouken.zone.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/party/{path}/settings")
@RequiredArgsConstructor
public class PartySettingsController {

    private final PartyService partyService;
    private final ModelMapper modelMapper;
    private final TagService tagService;
    private final TagRepository tagRepository;
    private final ZoneRepository zoneRepository;
    private final ObjectMapper objectMapper;

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
        return "redirect:/party/" + path + "/settings/description";
    }

    @GetMapping("/banner")
    public String partyBannerForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getPartyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(party);

        return "party/settings/banner";
    }

    @PostMapping("/banner")
    public String partyBannerSubmit (
            @CurrentAccount Account account,
            @PathVariable String path,
            String image,
            RedirectAttributes redirectAttributes) {

        Party party = partyService.getPartyToUpdate(account, path);
        partyService.updatePartyBanner(party, image);
        redirectAttributes.addFlashAttribute("message", "Banner image has been updated.");
        return "redirect:/party/" + path + "/settings/banner";
    }

    @PostMapping("/banner/enable")
    public String enablePartyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Party party = partyService.getPartyToUpdate(account, path);
        partyService.enablePartyBanner(party);
        return "redirect:/party/" + path + "/settings/banner";
    }

    @PostMapping("/banner/disable")
    public String disablePartyBanner(@CurrentAccount Account account, @PathVariable String path) {
        Party party = partyService.getPartyToUpdate(account, path);
        partyService.disablePartyBanner(party);
        return "redirect:/party/" + path + "/settings/banner";
    }

    @GetMapping("/tags")
    public String partyTagsForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Party party = partyService.getPartyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(party);

        model.addAttribute("tags", party.getTags().stream()
                .map(Tag::getTitle).collect(Collectors.toList()));
        List<String> allTagTitles = tagRepository.findAll().stream()
                .map(Tag::getTitle).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allTagTitles));
        return "party/settings/tags";
    }

    @PostMapping("/tags/add")
    @ResponseBody
    public ResponseEntity addTag(@CurrentAccount Account account, @PathVariable String path,
                                 @RequestBody TagForm tagForm) {
        Party party = partyService.getPartyToUpdateTag(account, path);
        Tag tag = tagService.findOrCreateNew(tagForm.getTagTitle());
        partyService.addTag(party, tag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/tags/remove")
    @ResponseBody
    public ResponseEntity removeTag(@CurrentAccount Account account, @PathVariable String path,
                                    @RequestBody TagForm tagForm) {
        Party party = partyService.getPartyToUpdateTag(account, path);
        Tag tag = tagRepository.findByTitle(tagForm.getTagTitle());
        if (tag == null) {
            return ResponseEntity.badRequest().build();
        }

        partyService.removeTag(party, tag);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/zones")
    public String partyZonesForm(@CurrentAccount Account account, @PathVariable String path, Model model)
            throws JsonProcessingException {
        Party party = partyService.getPartyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(party);
        model.addAttribute("zones", party.getZones().stream()
                .map(Zone::toString).collect(Collectors.toList()));
        List<String> allZones = zoneRepository.findAll().stream().map(Zone::toString).collect(Collectors.toList());
        model.addAttribute("whitelist", objectMapper.writeValueAsString(allZones));
        return "party/settings/zones";
    }

    @PostMapping("/zones/add")
    @ResponseBody
    public ResponseEntity addZone(@CurrentAccount Account account, @PathVariable String path,
                                  @RequestBody ZoneForm zoneForm) {
        Party party = partyService.getPartyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndCountry(zoneForm.getCityName(), zoneForm.getCountryName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        partyService.addZone(party, zone);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/party")
    public String partySettingForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getPartyToUpdate(account, path);
        model.addAttribute(account);
        model.addAttribute(party);
        return "party/settings/party";
    }
    
    @PostMapping("/zones/remove")
    @ResponseBody
    public ResponseEntity removeZone(@CurrentAccount Account account, @PathVariable String path,
                                     @RequestBody ZoneForm zoneForm) {
        Party party = partyService.getPartyToUpdateZone(account, path);
        Zone zone = zoneRepository.findByCityAndCountry(zoneForm.getCityName(), zoneForm.getCountryName());
        if (zone == null) {
            return ResponseEntity.badRequest().build();
        }

        partyService.removeZone(party, zone);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/party/publish")
    public String publishParty(@CurrentAccount Account account, @PathVariable String path,
                               RedirectAttributes attributes) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        partyService.publish(party);
        attributes.addFlashAttribute("message", "Party has been published.");
        return "redirect:/party/" + path + "/settings/party";
    }

    @PostMapping("/party/close")
    public String closeParty(@CurrentAccount Account account, @PathVariable String path,
                             RedirectAttributes attributes) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        partyService.close(party);
        attributes.addFlashAttribute("message", "Party has been closed.");
        return "redirect:/party/" + path + "/settings/party";
    }

    @PostMapping("/recruit/start")
    public String startRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                               RedirectAttributes attributes) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        if (!party.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "You can not update it several times in a hour.");
            return "redirect:/party/" + path + "/settings/party";
        }

        partyService.startRecruit(party);
        attributes.addFlashAttribute("message", "Recruitment start.");
        return "redirect:/party/" + path + "/settings/party";
    }

    @PostMapping("/recruit/stop")
    public String stopRecruit(@CurrentAccount Account account, @PathVariable String path, Model model,
                              RedirectAttributes attributes) {
        Party party = partyService.getPartyToUpdate(account, path);
        if (!party.canUpdateRecruiting()) {
            attributes.addFlashAttribute("message", "You can not update it several times in a hour.");
            return "redirect:/party/" + path + "/settings/party";
        }

        partyService.stopRecruit(party);
        attributes.addFlashAttribute("message", "Recruitment is over.");
        return "redirect:/party/" + path + "/settings/party";
    }

    @PostMapping("/party/path")
    public String updatePartyPath(@CurrentAccount Account account, @PathVariable String path,
                                  Model model, RedirectAttributes attributes) {
        String newPath = partyService.getNewPath();

        Party party = partyService.getPartyToUpdateStatus(account, path);
        if (!partyService.isValidPath(newPath)) {
            model.addAttribute(account);
            model.addAttribute(party);
            model.addAttribute("partyPathError", "Please try again.");
            return "party/settings/party";
        }

        partyService.updatePartyPath(party, newPath);
        attributes.addFlashAttribute("message", "The path has been updated");
        return "redirect:/party/" + newPath + "/settings/party";
    }

    @PostMapping("/party/title")
    public String updatePartyTitle(@CurrentAccount Account account, @PathVariable String path, String newTitle,
                                   Model model, RedirectAttributes attributes) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        if (!partyService.isValidTitle(newTitle)) {
            model.addAttribute(account);
            model.addAttribute(party);
            model.addAttribute("partyTitleError", "The title is invalid.");
            return "party/settings/party";
        }

        partyService.updatePartyTitle(party, newTitle);
        attributes.addFlashAttribute("message", "The title has been updated");
        return "redirect:/party/" + path + "/settings/party";
    }

    @PostMapping("/party/remove")
    public String removeParty(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Party party = partyService.getPartyToUpdateStatus(account, path);
        partyService.remove(party);
        return "redirect:/";
    }
}