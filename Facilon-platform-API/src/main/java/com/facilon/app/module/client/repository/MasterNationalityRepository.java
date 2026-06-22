package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterNationality;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterNationalityRepository extends JpaRepository<MasterNationality, Long> {
    Optional<MasterNationality> findBySsNationalityId(String ssNationalityId);

    /**
     * Look up by the {@code id} business column (NOT the {@code my_row_id} primary key).
     * Some flows (e.g. introduced registration) resolve nationality to this {@code id}
     * column, so we need an explicit query to disambiguate it from the inherited
     * {@code findById(Long)} which targets the {@code my_row_id} {@code @Id}.
     */
    @Query("select n from MasterNationality n where n.id = :idColumn")
    Optional<MasterNationality> findByIdColumn(@Param("idColumn") Integer idColumn);
}
