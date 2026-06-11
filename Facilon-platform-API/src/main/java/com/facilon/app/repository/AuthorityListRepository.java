package com.facilon.app.repository;

import com.facilon.app.model.AuthorityList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthorityListRepository extends JpaRepository<AuthorityList, Long> {

    Optional<AuthorityList> findByAuthorityName(String authorityName);
}
