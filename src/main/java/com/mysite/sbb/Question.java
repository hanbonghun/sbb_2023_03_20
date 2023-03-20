package com.mysite.sbb;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity  //JPA가 엔티티로 인식
public class Question {
    @Id //기본키
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // 해당 컬럼만의 시퀀스를 생성(IDENTITY) 하여 번호 증가
    private Integer id;

    @Column(length = 200)
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String content;
    private LocalDateTime createDate;

    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Answer> answerList;
}
