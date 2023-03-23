package com.mysite.sbb;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String root(){
        return "redirect:/question/list";
    }

    @GetMapping("/free")
    public String list(Model model, @RequestParam(value="page", defaultValue="1") int page, @RequestParam(value = "kw", defaultValue = "") String kw) {
        int pageSize=10;
        Page<Question> paging = this.questionService.getList(page,pageSize,kw);
        model.addAttribute("paging", paging);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("kw", kw);
        model.addAttribute("category","자유게시판");
        return "question_list";
    }

}
