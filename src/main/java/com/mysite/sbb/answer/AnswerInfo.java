package com.mysite.sbb.answer;

import com.mysite.sbb.answer.Answer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class AnswerInfo {
    private Integer questionId; // 질문글의 ID
    private String questionCategory; // 질문글의 카테고리
    private Integer answerId; // 답변글의 ID
    private Integer answerIndex; // 해당 질문글에서 몇 번째 답변글인지
    private String answerContent; // 답변글의 내용
    private LocalDateTime answerLocalDateTime;
    private long voteCount; // 추천 수
}
