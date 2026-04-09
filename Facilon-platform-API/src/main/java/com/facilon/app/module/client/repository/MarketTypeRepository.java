package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MarketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MarketTypeRepository extends JpaRepository<MarketType, Long> {

    List<MarketType> findByStatusOrderById(Boolean status);

    @Query("SELECT m FROM MarketType m WHERE m.id = :marketId")
    Optional<MarketType> findByMarketId(@Param("marketId") Integer marketId);
}
