package com.facilon.app.dto;

import com.facilon.app.model.RoleList;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@EqualsAndHashCode(callSuper = false)
 @NoArgsConstructor
@AllArgsConstructor
public class RoleListDto  extends  AuditableDto{

    private Long id;
    private Long parentListId;
    private String label;
    private int sequenceNo;
    private boolean isActive;
    private Collection<Long> authorityIds;
    // Constructors



    public RoleListDto convertToDto(RoleList roleList) {
        RoleListDto dto = new RoleListDto();
        dto.setId(roleList.getId());
        dto.setParentListId(roleList.getParentListId());
        dto.setLabel(roleList.getLabel());
        dto.setSequenceNo(roleList.getSequenceNo());
        dto.setActive(roleList.getIsActive());
        return dto;
    }

}
