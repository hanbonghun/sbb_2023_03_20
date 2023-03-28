package com.mysite.sbb.comment;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RequiredArgsConstructor
@RequestMapping("/comment")
@Controller
public class CommentController {
    private final CommentService commentService;
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/question")
    public String createCommentOnQuestion(@RequestParam("question_id") int questionId, @RequestParam("content") String content, Principal principal){
        SiteUser siteUser = this.userService.getSiteUser(principal);
        Question question = this.questionService.getQuestion(questionId);
        this.commentService.createQuestionComment(questionId,content,siteUser);
        return "redirect:/question/detail/" + questionId + "?category=" + question.getCategory() + "#question-comment-start";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/answer")
    public String createCommentOnAnswer(@RequestParam("answer_id") int answerId, @RequestParam("content") String content, Principal principal){
        SiteUser siteUser = this.userService.getSiteUser(principal);
        Answer answer = this.answerService.getAnswer(answerId);
        Question question = answer.getQuestion();
        int answerIndex = question.getAnswerList().indexOf(answer) + 1;
        int answerPage = (int) Math.ceil(answerIndex*1.0/5);
        this.commentService.createAnswerComment(answerId,content,siteUser);
        return "redirect:/question/detail/" + question.getId() + "?page="+answerPage+"#answer-comment-start_"+answerId;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/question/{id}")
    public String questionCommentVote(Principal principal, @PathVariable("id") Integer id) {
        SiteUser siteUser = this.userService.getSiteUser(principal);
        Comment comment = this.commentService.getComment(id);
        Question question = comment.getQuestion();
        this.commentService.vote(comment, siteUser);
        return "redirect:/question/detail/" + question.getId() + "?category=" + question.getCategory() + "#question-comment-start";

    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/answer/{id}")
    public String answerCommentVote(Principal principal, @PathVariable("id") Integer id) {
        SiteUser siteUser = this.userService.getSiteUser(principal);
        Comment comment = this.commentService.getComment(id);
        Answer answer = comment.getAnswer();
        Question question = answer.getQuestion();
        int answerIndex = question.getAnswerList().indexOf(answer) + 1;
        int answerPage = (int) Math.ceil(answerIndex*1.0/5);
        this.commentService.vote(comment, siteUser);
        return "redirect:/question/detail/" + question.getId() + "?page="+answerPage+"#answer-comment-start_"+answer.getId();

    }
}
