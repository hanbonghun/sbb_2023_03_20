package com.mysite.sbb.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SocialController {
    @GetMapping("/login/oauth2/code/google")
    public String googleLogin(@RequestParam String code){
        return "";
    }
}
