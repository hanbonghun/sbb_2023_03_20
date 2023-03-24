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

    @GetMapping("/study")
    public String study(){
        return "study_list";
    }

    @GetMapping("/tips")
    public String tip(){
        return "study_list";
    }

}
