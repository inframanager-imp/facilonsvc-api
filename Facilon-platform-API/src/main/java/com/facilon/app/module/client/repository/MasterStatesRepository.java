package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterStates;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterStatesRepository extends JpaRepository<MasterStates, Long> {

    List<MasterStates> findBySsCountryValueOrderBySsName(String countryValue);
}
