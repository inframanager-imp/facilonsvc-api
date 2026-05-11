package com.facilon.app.module.serviceprovider.repository;

import com.facilon.app.module.serviceprovider.model.ServiceProviderUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceProviderUserRepository extends JpaRepository<ServiceProviderUser, Long> {
    boolean existsByOfficialEmail(String officialEmail);
}
