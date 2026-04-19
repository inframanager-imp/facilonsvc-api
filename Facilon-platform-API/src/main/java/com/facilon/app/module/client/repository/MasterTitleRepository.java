package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterTitleRepository extends JpaRepository<MasterTitle, Long> {

    /**
     * Look up a title by its business {@code id} column (not the auto-generated {@code my_row_id}).
     * Used to resolve the Dataverse {@code ss_titleid} GUID for {@code @odata.bind} lookups,
     * matching Laravel's {@code DB::table('master_title')->where('id', $local_id)->first()}.
     */
    @Query("SELECT m FROM MasterTitle m WHERE m.id = :id")
    Optional<MasterTitle> findByBusinessId(@Param("id") Integer id);
}
