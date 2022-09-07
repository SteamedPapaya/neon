package com.mouken.settings;

import com.mouken.account.AccountService;
import com.mouken.account.CurrentUser;
import com.mouken.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/settings")
public class SettingsController {

    @InitBinder("passwordForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(new PasswordFormValidator());
    }

    private final AccountService accountService;

    @GetMapping("/profile")
    public String profileUpdateForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new Profile(account));
        return "settings/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @CurrentUser Account account,
            @Valid @ModelAttribute Profile profile,
            Errors errors,
            Model model,
            RedirectAttributes redirectAttributes) {


	    if(errors.hasErrors()) {
            log.info("updateProfilehasErrors");
            model.addAttribute(account);
            return "settings/profile";
        }

    	accountService.updateProfile(account, profile);
        redirectAttributes.addFlashAttribute("message", "Profile has updated.");
	    return "redirect:/settings/profile";
    }

    @GetMapping("/password")
    public String updatePasswordForm(@CurrentUser Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new PasswordForm());
        return "settings/password";
    }

    @PostMapping("/password")
    public String updatePassword(@CurrentUser Account account, @Validated PasswordForm passwordForm, Errors errors,
                                 Model model, RedirectAttributes attributes) {

        if (errors.hasErrors()) {
            model.addAttribute(account);
            return"settings/password";
        }

        accountService.updatePassword(account, passwordForm.getNewPassword());
        attributes.addFlashAttribute("message", "Password has been updated.");
        return "redirect:/settings/password";
    }
}
