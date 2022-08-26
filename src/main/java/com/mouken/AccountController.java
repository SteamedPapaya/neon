package com.mouken;

import com.mouken.domain.SignUpForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute(new SignUpForm());
        return "account/signup";
    }
}
