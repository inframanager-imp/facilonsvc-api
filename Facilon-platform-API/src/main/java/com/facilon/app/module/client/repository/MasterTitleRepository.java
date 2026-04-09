package com.facilon.app.module.client.repository;

import com.facilon.app.module.client.model.master.MasterTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterTitleRepository extends JpaRepository<MasterTitle, Long> {
}
