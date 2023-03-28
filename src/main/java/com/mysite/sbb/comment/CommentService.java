package com.mysite.sbb.comment;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final AnswerService answerService;
    private final QuestionService questionService;

    public Comment getComment(Integer id){
        Optional<Comment> comment = this.commentRepository.findById(id);
        if (comment.isPresent()) {
            return comment.get();
        } else {
            throw new DataNotFoundException("comment not found");
        }
    }

    public void createAnswerComment(int answerId, String content, SiteUser siteUser) {
        Answer answer = this.answerService.getAnswer(answerId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAnswer(answer);
        comment.setAuthor(siteUser);
        comment.setCreateDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public void createQuestionComment(int questionId, String content, SiteUser siteUser) {
        Question question = this.questionService.getQuestion(questionId);
        Comment comment = new Comment();
        comment.setContent(content);
        comment.setQuestion(question);
        comment.setAuthor(siteUser);
        comment.setCreateDate(LocalDateTime.now());
        commentRepository.save(comment);
    }

    public void vote(Comment comment, SiteUser siteUser) {
        comment.getVoter().add(siteUser);
        this.commentRepository.save(comment);
    }
}
