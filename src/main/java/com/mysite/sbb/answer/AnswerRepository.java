package com.mysite.sbb.answer;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer,Integer> {
    @Query("select q from Answer q join q.author u where u.nickname like %:nickname%")
    Page<Answer> findByAuthorNickname(@Param("nickname") String nickname, Pageable pageable);
}
