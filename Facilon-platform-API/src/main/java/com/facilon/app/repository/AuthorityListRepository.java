package com.facilon.app.repository;

import com.facilon.app.model.AuthorityList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityListRepository extends JpaRepository<AuthorityList, Long> {
}
