package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SiteUser createUser(String username, String email, String nickname, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setNickname(nickname);
        this.userRepository.save(user);
        return user;
    }

    public SiteUser createGoogleUser (String email, String nickname,String google_id){
        SiteUser user = new SiteUser();
        user.setEmail(email);
        user.setNickname(nickname);
        user.setGoogleId(google_id);
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUserByUsername(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser getUserByEmail(String email){
        Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
        if(siteUser.isPresent()){
            SiteUser user = siteUser.get();
            if(user.getGoogleId()==null) user.setGoogleId(email);
            return user;
        }else{
            return null;
        }
    }

    public SiteUser findByGoogleId(String google_id){
        Optional<SiteUser> siteUser = this.userRepository.findByGoogleId(google_id);
        if(siteUser.isPresent()){
            SiteUser user = siteUser.get();
            return user;
        }else{
            return null;
        }
    }

    public SiteUser findById(Long user_id){
        Optional<SiteUser> siteUser = this.userRepository.findById(user_id);
        if(siteUser.isPresent()){
            SiteUser user = siteUser.get();
            return user;
        }else{
            return null;
        }
    }


    public boolean isUsernameExists(String username) {
        return this.userRepository.findByusername(username).isPresent();
    }
    public boolean isEmailExists(String email) {
        return this.userRepository.findByEmail(email).isPresent();
    }
    public boolean isNicknameExists(String nickname) {
        return this.userRepository.findByNickname(nickname).isPresent();

    }

    //principal객체에서 현재 로그인 한 사용자를 불러옴
    public SiteUser getSiteUser(Principal principal) {
        SiteUser siteUser = null;
        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authenticationToken = (OAuth2AuthenticationToken) principal;
            OAuth2User oAuth2User = authenticationToken.getPrincipal();
            String provider = authenticationToken.getAuthorizedClientRegistrationId(); //google
            String providerId = oAuth2User.getAttribute("sub"); //google_id (구글 로그인 시 사용자 별로 고유하게 식별되는 id)

            if(provider.equals("google")){
                siteUser = this.findByGoogleId(providerId);
            }
        } else if(principal instanceof UsernamePasswordAuthenticationToken){
            siteUser = this.getUserByUsername(principal.getName());
        }
        return siteUser;
    }

    //비밀번호 변경
    public void updatePassword(SiteUser siteUser,String password){
        siteUser.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(siteUser);
    }

    //회원 삭제
    public void delete(Long userId,Principal principal){
        SiteUser curr = this.getSiteUser(principal);
        SiteUser siteUser = this.findById(userId);
        if(curr.getId()!=userId) return ;

        this.userRepository.delete(siteUser);
    }
}