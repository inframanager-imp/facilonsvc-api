package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterCities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterCitiesRepository extends JpaRepository<MasterCities, Long> {

    List<MasterCities> findBySsStateValueOrderBySsName(String stateValue);
}
