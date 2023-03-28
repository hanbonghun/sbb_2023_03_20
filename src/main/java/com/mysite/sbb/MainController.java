package com.mysite.sbb;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/")
    public String root(Model model, Principal principal){
        int pageSize=5;
        Page<Question> qnaPaging = this.questionService.getList(1,pageSize,"","qna");
        Page<Question> freePaging = this.questionService.getList(1,pageSize,"","free");
        Page<Question> tipsPaging = this.questionService.getList(1,pageSize,"","tips");
        Page<Question> studyPaging = this.questionService.getList(1,pageSize,"","study");
        SiteUser siteUser = this.userService.getSiteUser(principal);
        model.addAttribute("qnaPaging", qnaPaging);
        model.addAttribute("freePaging", freePaging);
        model.addAttribute("tipsPaging", tipsPaging);
        model.addAttribute("studyPaging", studyPaging);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("kw", "");
        model.addAttribute("category","qna");
        model.addAttribute("siteUser", siteUser);
        return "home";
    }

    @GetMapping("/study")
    public String study(){
        return "study_list";
    }

    @GetMapping("/tips")
    public String tip(){
        return "study_list";
    }

}
