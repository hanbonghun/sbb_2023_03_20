package com.mysite.sbb.comment;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/comment")
@Controller
public class CommentController {
    @PostMapping("/question/create")
    public String createCommentOnQuestion(@RequestParam("question_id") int questionId){
        return "";
    }
    @PostMapping("/answer/create")
    public String createCommentOnAnswer(@RequestParam("answer_id") int answerId){
        return "";
    }
}
