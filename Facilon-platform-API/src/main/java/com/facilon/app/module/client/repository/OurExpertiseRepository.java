package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.content.OurExpertise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OurExpertiseRepository extends JpaRepository<OurExpertise, Long> {

    List<OurExpertise> findByStatusOrderById(Integer status);
}
