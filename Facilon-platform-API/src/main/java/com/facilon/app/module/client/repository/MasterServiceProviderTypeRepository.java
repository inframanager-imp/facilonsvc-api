package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterServiceProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterServiceProviderTypeRepository extends JpaRepository<MasterServiceProviderType, Long> {

    Optional<MasterServiceProviderType> findBySsProviderId(String ssProviderId);
}
