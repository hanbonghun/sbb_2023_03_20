package com.mysite.sbb.user;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class Oauth2UserSecurityService extends DefaultOAuth2UserService {
    private  UserRepository userRepository;
    private  UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();    //google
        String providerId = oAuth2User.getAttribute("sub");
        String username = provider+"_"+providerId;  			// 사용자가 입력한 적은 없지만 만들어준다
        String email = oAuth2User.getAttribute("email");
//        System.out.println("provider : "+ provider);
//        System.out.println("providerId : "+ providerId);
//        System.out.println("username : "+ username);
//        System.out.println("email = " + email);
        return super.loadUser(userRequest);
    }
}
