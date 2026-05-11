package com.facilon.app.module.serviceprovider.repository;

import com.facilon.app.module.serviceprovider.model.SpConsentUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpConsentUserRepository extends JpaRepository<SpConsentUser, Long> {
}
