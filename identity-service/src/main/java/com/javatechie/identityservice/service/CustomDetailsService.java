package com.javatechie.identityservice.service;

import com.javatechie.identityservice.config.CustomUserDetails;
import com.javatechie.identityservice.entity.UserCredential;
import com.javatechie.identityservice.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomDetailsService implements UserDetailsService {
    @Autowired
    private UserCredentialRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserCredential> user = repository.findByName(username);

        return user.map(CustomUserDetails::new).orElseThrow(() -> new UsernameNotFoundException("User not found with name: " + username));
    }
}
