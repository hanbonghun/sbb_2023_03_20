package com.mysite.sbb;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserRepository;
import com.mysite.sbb.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final UserService userService;

    @Async
    public void sendMail(String email) {
        SimpleMailMessage simpleMessage = new SimpleMailMessage();
        SiteUser siteUser = this.userService.getUserByEmail(email);
        String tempPassword = getTempPassword();

        // simpleMessage.setFrom("보낸사람@naver.com"); // NAVER, DAUM, NATE일 경우 넣어줘야 함
        simpleMessage.setTo(email);
        simpleMessage.setSubject("임시 비밀번호 안내 이메일 입니다.");
        simpleMessage.setText("임시 비밀번호 : "+tempPassword);
        javaMailSender.send(simpleMessage);

        this.userService.updatePassword(siteUser,tempPassword);
    }

    public String getTempPassword(){
        char[] charSet = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };

        String str = "";

        // 문자 배열 길이의 값을 랜덤으로 10개를 뽑아 구문을 작성함
        int idx = 0;
        for (int i = 0; i < 10; i++) {
            idx = (int) (charSet.length * Math.random());
            str += charSet[idx];
        }
        return str;
    }

}
