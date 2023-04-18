package com.mysite.sbb.comment;

import com.mysite.sbb.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    @Query("SELECT q FROM Comment q WHERE q.author.id = :authorId")
    List<Comment> findByAuthorId(@Param("authorId") Long authorId);
}
