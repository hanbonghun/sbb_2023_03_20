package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@Entity
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createDate;
    @ManyToOne  //답변과 질문은 N:1관계이므로 , 질문 엔티티와 연결된 속성이라는 것을 명시하는 어노테이션
    private Question question;

    @ManyToOne
    private SiteUser author;
    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

}
