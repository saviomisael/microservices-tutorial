package com.javatechie.identityservice.controller;

import com.javatechie.identityservice.dto.AuthRequest;
import com.javatechie.identityservice.entity.UserCredential;
import com.javatechie.identityservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public String addNewUser(@RequestBody UserCredential credential) {
        return authService.saveUser(credential);
    }

    @PostMapping("/token")
    public String getToken(@RequestBody AuthRequest request) {
        Authentication authenticate = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        if (authenticate.isAuthenticated())
            return authService.generateToken(request.getUsername());

        throw new RuntimeException("user invalid access");
    }

    @GetMapping("/validateToken")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);

        return "Token is valid";
    }
}
