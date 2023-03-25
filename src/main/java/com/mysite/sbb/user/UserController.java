package com.mysite.sbb.user;

import jakarta.validation.Valid;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm) {
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        if(this.userService.isUsernameExists(userCreateForm.getUsername())) {
            bindingResult.rejectValue("username", "duplicateUsername",
                    "이미 존재하는 ID입니다");
            return "signup_form";
        }
        if(this.userService.isEmailExists(userCreateForm.getEmail())) {
            bindingResult.rejectValue("email", "duplicateEmail",
                    "이미 존재하는 메일입니다");
            return "signup_form";
        }
        if(this.userService.isNicknameExists(userCreateForm.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicateNickname",
                    "이미 존재하는 닉네임입니다");
            return "signup_form";
        }
        userService.createUser(userCreateForm.getUsername(),
                userCreateForm.getEmail(), userCreateForm.getNickname(), userCreateForm.getPassword1());
        return "redirect:/";
    }

    @GetMapping("/login/oauth2/code/google")
    public String signupNickname(@RequestParam String code){
        System.out.println("성공"+code);
        return "";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/signupNickname")
    public String signupNickname(Principal principal, Model model, UserCreateForm userCreateForm){
        //Outh유저중 해당 메일로 가입한 유저가 존재한다면
        SiteUser siteUser = null;
        OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) principal;
        OAuth2User oAuth2User = authenticationToken.getPrincipal();
        String provider = authenticationToken.getAuthorizedClientRegistrationId(); //google
        String providerId = oAuth2User.getAttribute("sub"); //google_id (구글 로그인 시 사용자 별로 고유하게 식별되는 id)
        System.out.println("providerName = " + provider);
        System.out.println("providerId get = " + providerId);
        if(provider.equals("google")){
            System.out.println("구글 ㅎㅇ");
            siteUser = this.userService.findByGoogleId(providerId);
        }
        System.out.println("사용자 존재 유무"+ siteUser);
        //이미 소셜 계정이 가입되어 있다면
        if(siteUser==null) return "signup_nickname_form";
        else  return "redirect:/";
    }
    @PostMapping("/signupNickname")
    public String signupNickname(@Valid UserCreateForm userCreateForm, BindingResult bindingResult,Principal principal){
        if(this.userService.isNicknameExists(userCreateForm.getNickname())) {
            bindingResult.rejectValue("nickname", "duplicateNickname",
                    "이미 존재하는 닉네임입니다");
            return "signup_nickname_form";
        }
        OAuth2User oAuth2User = ((OAuth2AuthenticationToken) principal).getPrincipal();
        String email = oAuth2User.getAttribute("email");
        System.out.println("principal.getName() = " + principal.getName());
        String google_id = oAuth2User.getAttribute("sub");
        this.userService.createGoogleUser(email,userCreateForm.getNickname(), google_id);
        System.out.println("생성완료");
        return "redirect:/";
    }

}