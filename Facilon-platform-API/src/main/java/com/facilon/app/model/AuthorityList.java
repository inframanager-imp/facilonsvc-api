package com.facilon.app.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "AuthorityList")
@Table(name = "authority_list")
@Data
@NoArgsConstructor
public class AuthorityList  extends TenantEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authority_list_id")
    private Long id;

    @Column(name = "authority_name", unique = true)
    private String authorityName;
    @Column(name = "resource_pattern", unique = true)
    private String resourcePattern;
    @Column(name = "description")
    private String description;



    public AuthorityList(String authorityName, String description) {
        this.authorityName = authorityName;
        this.description = description;
    }
}
