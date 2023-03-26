package com.mysite.sbb.user;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.answer.AnswerInfo;
import com.mysite.sbb.question.QuestionService;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final AuthenticationManager authenticationManager;

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

        // 로그인 성공
        return "redirect:/";
    }

    @GetMapping("/login/oauth2/code/google")
    public String signupNickname(@RequestParam String code){
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
        if(provider.equals("google")){
            siteUser = this.userService.findByGoogleId(providerId);
        }
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
        String google_id = oAuth2User.getAttribute("sub");
        this.userService.createGoogleUser(email,userCreateForm.getNickname(), google_id);
        return "redirect:/";
    }

    @GetMapping("/profile")
    public String profile(Principal principal,Model model,@RequestParam("nickname") String nickname, @RequestParam(value = "page", defaultValue = "1") int page){
        SiteUser siteUser = this.userService.getSiteUser(principal);
        int pageSize=10;
        Page<Question> questionPaging = this.questionService.getQuestionListByNickname(page, pageSize,siteUser.getNickname());
        Page<Answer> answerPaging = this.answerService.getAnswerListByNickname(page, pageSize,nickname);
        List<Answer> answerList = answerPaging.getContent();
        List<AnswerInfo> questionAnswerInfoList = new ArrayList<>();

        for (Answer answer : answerList) {
            Question question = answer.getQuestion();
            int answerIndex = question.getAnswerList().indexOf(answer) + 1;
            questionAnswerInfoList.add(new AnswerInfo(question.getId(), question.getCategory(), answer.getId(), (int) Math.ceil(answerIndex*1.0/5),answer.getContent(), answer.getCreateDate(), answer.getVoter().size()));
        }
        model.addAttribute("siteUser", siteUser);
        model.addAttribute("questionPaging", questionPaging);
        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("questionAnswerInfoList", questionAnswerInfoList);
        model.addAttribute("pageSize", pageSize);
        return "profile";
    }

}