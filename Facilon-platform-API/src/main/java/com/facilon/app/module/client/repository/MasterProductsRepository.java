package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterProducts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterProductsRepository extends JpaRepository<MasterProducts, Long> {

    Optional<MasterProducts> findBySsProductId(String ssProductId);
}
