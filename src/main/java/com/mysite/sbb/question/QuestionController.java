package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;


@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final AnswerService answerService;

    // 쿠키에서 중복 방문 체크
    private boolean isHit(HttpServletRequest req, String viewKey) {
        // 쿠키 가져오기
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(viewKey)) {
                    return true;
                }
            }
        }
        return false;
    }

    @GetMapping("/list")
    public String list(Model model, Principal principal, @RequestParam(value="page", defaultValue="1") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(value="category", defaultValue = "qna") String category) {
        int pageSize=10;
        Page<Question> paging = this.questionService.getList(page,pageSize,kw,category);
        SiteUser siteUser = this.userService.getSiteUser(principal);
        model.addAttribute("paging", paging);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("kw", kw);
        model.addAttribute("category",category);
        model.addAttribute("siteUser", siteUser);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model, HttpServletRequest req, HttpServletResponse res, @PathVariable("id") Integer id, @RequestParam(value = "page", defaultValue = "1")int page, @RequestParam(value = "category", required = false) String category,AnswerForm answerForm, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        // 한 페이지당 출력할 답변의 개수
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Answer> paging = question.getAnswers(pageable);
        // 현재 로그인된 사용자 정보 확인
        SiteUser siteUser = this.userService.getSiteUser(principal);
        //조회수
        String viewKey = "_q" + id;

        if (!isHit(req, viewKey)) { // 현재 세션으로 처음 방문
            // 쿠키 생성
            Cookie viewCookie = new Cookie(viewKey, "true");
            // 쿠키 유효기간 설정 (1일)
            viewCookie.setMaxAge(60 * 60 * 24);
            // 쿠키 적용
            res.addCookie(viewCookie);

            // 조회수 증가
            questionService.increaseViews(id);
        }
        model.addAttribute("siteUser",siteUser);
        model.addAttribute("question", question);
        model.addAttribute("paging",paging);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("category",category);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model,QuestionForm questionForm, Principal principal, @RequestParam(value ="category") String category){
        SiteUser siteUser = this.userService.getSiteUser(principal);
        model.addAttribute("category", category);
        model.addAttribute("siteUser", siteUser);
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @RequestParam(value ="category") String category) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = this.userService.getSiteUser(principal);
        Question q = this.questionService.create(questionForm.getSubject(), questionForm.getContent(),siteUser, category);
        return "redirect:/question/detail/"+ q.getId()+"?category="+category;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getSiteUser(principal);
        if(!question.getAuthor().getNickname().equals(siteUser.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getSiteUser(principal);

        if (!question.getAuthor().getNickname().equals(siteUser.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id, @RequestParam("category") String category) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getSiteUser(principal);

        if (!question.getAuthor().getNickname().equals(siteUser.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/question/list?category=" + category;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getSiteUser(principal);
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
