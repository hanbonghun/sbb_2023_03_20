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

    public SiteUser create(String username, String email, String password) {
        SiteUser user = new SiteUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
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
}