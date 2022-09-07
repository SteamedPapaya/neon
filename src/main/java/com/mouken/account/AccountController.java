package com.mouken.account;

import com.mouken.View;
import com.mouken.domain.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final SignUpFormValidator signUpFormValidator;

    @InitBinder("signUpForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(signUpFormValidator);
    }

    @GetMapping("/sign-up")
    public String signUpForm(Model model) {
        log.info("signUpForm");

        model.addAttribute(new SignUpForm());
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public String signUpSubmit(
            @Validated SignUpForm signUpForm,
            BindingResult bindingResult) {
        log.info("signUpSubmit");

        if (bindingResult.hasErrors()) {
            return "account/sign-up";
        }

        Account account = accountService.processNewAccount(signUpForm);
        accountService.login(account);
        return "redirect:/check-email";
    }




    @GetMapping("/check-email-token")
    public String checkEmailToken(String token, String email, Model model, RedirectAttributes redirectAttributes) {

        // check account of email
        Account account = accountRepository.findByEmail(email);

        if (account == null) {
            redirectAttributes.addFlashAttribute("error", "wrong.email");
            return "redirect:/check-email";
        }

        // check token
        if (!account.isValidToken(token)) {
            redirectAttributes.addFlashAttribute("error", "wrong.token");
            return "redirect:/check-email";
        }

        // verify account
        accountService.completeSignUp(account);
        redirectAttributes.addFlashAttribute("info", "Complete");
        return "redirect:/check-email";
    }

    @GetMapping("/check-email")
    public String checkEmail(@CurrentUser Account account, Model model) {
        if (account != null) {
            model.addAttribute(account);
            model.addAttribute("username", account.getUsername());
            model.addAttribute("email", account.getEmail());
        }
        return "account/check-email";
    }

    @GetMapping("/send-email")
    public String sendEmail(@CurrentUser Account account, Model model, RedirectAttributes redirectAttributes) {

        if (account.isEmailVerified()) {
            return "redirect:/";
        }

        if (!account.canSendConfirmEmail()) {
            redirectAttributes.addFlashAttribute("error", "You can get an email once per in 15 minutes");
            return "redirect:/check-email";
        }

        accountService.sendSignUpConfirmEmail(account);
        redirectAttributes.addFlashAttribute("info", "Email has sent");
        return "redirect:/check-email";
    }

    @GetMapping("profile/{username}")
    public String viewProfile(
            @PathVariable String username,
            @CurrentUser Account account,
            Model model) {
        Account foundAccount = accountRepository.findByUsername(username);
        if (account == null) {
            throw new IllegalArgumentException("This user does not exist");
        }

        model.addAttribute(foundAccount);
        model.addAttribute("isOwner", foundAccount.equals(account));
        return "account/profile";
    }


}
