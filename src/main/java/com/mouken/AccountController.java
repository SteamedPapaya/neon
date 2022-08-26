package com.mouken;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AccountController {

    @GetMapping("/signup")
    public String signupForm() {
        return "account/signup";
    }
}
