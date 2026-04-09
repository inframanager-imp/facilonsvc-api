
package com.facilon.app.service;

import com.facilon.app.repository.AuthorizedUserRepository;
import com.facilon.app.model.AuthorizedUser;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final AuthorizedUserRepository authorizedUserRepository;

    public UserService(AuthorizedUserRepository authorizedUserRepository) {
        this.authorizedUserRepository = authorizedUserRepository;
    }

    public Optional<AuthorizedUser> findByEmailOrLoginId(String emailOrLoginId) {
        return authorizedUserRepository.findByEmailIdOrLoginId(emailOrLoginId, emailOrLoginId);
    }
    public Optional<AuthorizedUser> findByEmailOrLoginId(String email,String loginId) {
        return authorizedUserRepository.findByEmailIdOrLoginId(email, loginId);
    }

    public AuthorizedUser findById(Long id) {
        return authorizedUserRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
    public AuthorizedUser findByEmail(String email) {
        return authorizedUserRepository.findByEmailId(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }
}

