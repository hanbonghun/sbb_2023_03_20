package com.mysite.sbb.question;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    private SiteUser getSiteUser(Principal principal) {
        SiteUser siteUser = null;
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) principal;
            OAuth2User oAuth2User = authenticationToken.getPrincipal();
            String provider = authenticationToken.getAuthorizedClientRegistrationId(); //google
            String providerId = oAuth2User.getAttribute("sub"); //google_id (구글 로그인 시 사용자 별로 고유하게 식별되는 id)

            if(provider.equals("google")){
                siteUser = this.userService.findByGoogleId(providerId);
            }
        } else if(principal instanceof UsernamePasswordAuthenticationToken){
            siteUser = this.userService.getUser(principal.getName());
        }
        return siteUser;
    }
    // 현재 세션으로 페이지에 접속한 적이 있는지를 확인
    private boolean isHit(HttpServletRequest req, String viewKey){
        return (String)req.getSession().getAttribute(viewKey) != null;
    }

    // 현재 세션으로 페이지에 처음 접속한다면 viewKey를 key로 추가
    private void setHit(HttpServletRequest req, String viewKey){
        req.getSession().setAttribute(viewKey,true);
    }

    @GetMapping("/list")
    public String list(Model model, @RequestParam(value="page", defaultValue="1") int page, @RequestParam(value = "kw", defaultValue = "") String kw, @RequestParam(value="category", defaultValue = "qna") String category) {
        int pageSize=10;
        Page<Question> paging = this.questionService.getList(page,pageSize,kw,category);
        model.addAttribute("paging", paging);
        model.addAttribute("pageSize",pageSize);
        model.addAttribute("kw", kw);
        model.addAttribute("category",category);
        return "question_list";
    }

    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,HttpServletRequest req, @PathVariable("id") Integer id, @RequestParam(value = "page", defaultValue = "1")int page, AnswerForm answerForm,Principal principal) {
        Question question = this.questionService.getQuestion(id);
        // 한 페이지당 출력할 답변의 개수
        int pageSize = 5;
        Pageable pageable = PageRequest.of(page-1, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Answer> paging = question.getAnswers(pageable);
        model.addAttribute("question", question);
        model.addAttribute("paging",paging);
        model.addAttribute("pageSize",pageSize);
        // 현재 로그인된 사용자 정보 확인
        SiteUser siteUser = getSiteUser(principal);
        model.addAttribute("siteUser",siteUser);

        //조회수
        String viewKey = "_q"+id;
        if(!isHit(req,viewKey)){  //현재 세션으로 처음 방문

        }
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(Model model,QuestionForm questionForm, @RequestParam(value ="category") String category){
        model.addAttribute("category", category);
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal, @RequestParam(value ="category") String category) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        SiteUser siteUser = getSiteUser(principal);
        Question q = this.questionService.create(questionForm.getSubject(), questionForm.getContent(),siteUser, category);
        return "redirect:/question/detail/"+ q.getId();
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = getSiteUser(principal);
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
        SiteUser siteUser = getSiteUser(principal);

        if (!question.getAuthor().getNickname().equals(siteUser.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = getSiteUser(principal);

        if (!question.getAuthor().getNickname().equals(siteUser.getNickname())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = getSiteUser(principal);
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }
}
