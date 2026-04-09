package com.facilon.app.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Entity(name = "RoleList")
@Table(name = "role_list")
@Data
@NoArgsConstructor
public class RoleList extends TenantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_list_id")
    private Long id;

    @Column(name = "parent_list_id")
    private Long parentListId;

    @Column(name = "label")
    private String label;

    @Column(name = "sequence_no")
    private int sequenceNo;

    @Column(name = "is_active")
    private Boolean isActive;


    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "role_authority",
            joinColumns = @JoinColumn(name = "role_list_id"),
            inverseJoinColumns = @JoinColumn(name = "authority_list_id")
    )
    private Collection<AuthorityList> authorities;

    public RoleList(Long parentListId, String label, int sequenceNo, Boolean isActive) {
        this.parentListId = parentListId;
        this.label = label;
        this.sequenceNo = sequenceNo;
        this.isActive = isActive;
    }

    public RoleList(Long id) {
        this.id = id;
    }
}
