package com.jinwon.ssoauth.web;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/user/me")
    public Principal user(Principal principal) {
        return principal;
    }

}
