package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.RegistrationSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RegistrationSessionRepository extends JpaRepository<RegistrationSession, Long> {
    
    Optional<RegistrationSession> findByUniqueCode(String uniqueCode);
    
    Optional<RegistrationSession> findByEmail(String email);
    
    Optional<RegistrationSession> findByEmailAndRegistrationCompletedFalse(String email);
}
