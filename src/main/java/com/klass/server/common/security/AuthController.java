package com.klass.server.common.security;

import com.klass.server.common.security.JWTToken;
import com.klass.server.common.security.TokenService;
import com.klass.server.user.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;


    @PostMapping
    public ResponseEntity authenticate(@RequestBody UserAuth userAuth) {
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                userAuth.getEmail(),
                userAuth.getPassword()
        );
        Authentication authUser = authenticationManager.authenticate(authenticationToken);
        // Generate JWT
        String jwt = tokenService.generateToken((UserDetails) authUser.getPrincipal());
        return ResponseEntity.ok(new JWTToken(jwt));

    }

}
