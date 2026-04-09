package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterCountries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterCountriesRepository extends JpaRepository<MasterCountries, Long> {
    
    Optional<MasterCountries> findById(Integer id);
}
