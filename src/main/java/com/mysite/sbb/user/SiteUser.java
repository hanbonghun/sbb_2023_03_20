package com.mysite.sbb.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length =100)
    private String username;

    private String password;

    @Column(unique = true, length =100)
    private String nickname;

    @Column(unique = true, length =100)
    private String email;

    @Column(unique = true, length =100)
    private String googleId;
}
