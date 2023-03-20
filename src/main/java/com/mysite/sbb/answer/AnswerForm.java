package com.mysite.sbb.answer;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AnswerForm {
    @NotEmpty(message = "내용은 필수항목입니다.")
    String content;
}
