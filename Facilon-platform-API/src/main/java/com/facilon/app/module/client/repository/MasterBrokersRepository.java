package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterBrokers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterBrokersRepository extends JpaRepository<MasterBrokers, Long> {
    
    /**
     * Find broker by broker ID (ssBrokerId)
     */
    Optional<MasterBrokers> findBySsBrokerId(String brokerId);
}
