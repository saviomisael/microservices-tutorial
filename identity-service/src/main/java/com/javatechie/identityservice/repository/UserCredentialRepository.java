package com.javatechie.identityservice.repository;

import com.javatechie.identityservice.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialRepository extends JpaRepository<UserCredential, Integer> {
    public Optional<UserCredential> findByName(String username);
}
