package com.mysite.sbb.user;

import com.mysite.sbb.DataNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

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

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser findByEmail(String email){
        Optional<SiteUser> siteUser = this.userRepository.findByEmail(email);
        if(siteUser.isPresent()){
            SiteUser user = siteUser.get();
            if(user.getGoogleId()==null) user.setGoogleId(email);
            return user;
        }else{
            throw new DataNotFoundException("siteuser not found");
        }
    }

    public SiteUser findByGoogleId(String google_id){
        Optional<SiteUser> siteUser = this.userRepository.findByGoogleId(google_id);
        System.out.println("google_id = " + google_id);
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
}