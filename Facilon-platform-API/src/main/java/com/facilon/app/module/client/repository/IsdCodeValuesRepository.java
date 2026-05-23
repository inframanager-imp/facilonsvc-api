package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.IsdCodeValues;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IsdCodeValuesRepository extends JpaRepository<IsdCodeValues, Long> {

    List<IsdCodeValues> findByStatusOrderByCodeValue(Integer status);

    Optional<IsdCodeValues> findFirstByCountryNameIgnoreCase(String countryName);
}
