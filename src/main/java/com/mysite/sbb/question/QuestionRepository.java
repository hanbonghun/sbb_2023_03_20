package com.mysite.sbb.question;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface QuestionRepository extends JpaRepository<Question,Integer> {
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    //닉네임으로 글목록 반환
    @Query("select q from Question q join q.author u where u.nickname like %:nickname%")
    Page<Question> findByAuthorNickname(@Param("nickname") String nickname, Pageable pageable);

    @Query("SELECT q FROM Question q WHERE q.author.id = :authorId")
    List<Question> findByAuthorId(@Param("authorId") Long authorId);
}
