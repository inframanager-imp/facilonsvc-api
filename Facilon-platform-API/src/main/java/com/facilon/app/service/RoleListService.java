package com.facilon.app.service;

import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.dto.RoleListDto;
import com.facilon.app.model.AuthorityList;
import com.facilon.app.model.RoleList;
import com.facilon.app.repository.AuthorityListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoleListService {

    @Autowired
    private RoleListRepository roleListRepository;

    @Autowired
    private AuthorityListRepository authorityListRepository;

    public List<RoleListDto> getAllRoles() {
        return roleListRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Optional<RoleListDto> getRoleById(Long id) {
        return roleListRepository.findById(id).map(this::convertToDto);
    }

    public RoleListDto saveRole(RoleListDto roleDto) {
        RoleList role = convertToEntity(roleDto);
        RoleList savedRole = roleListRepository.save(role);
        return convertToDto(savedRole);
    }

    public void deleteRole(Long id) {
        roleListRepository.deleteById(id);
    }

    private RoleListDto convertToDto(RoleList role) {
        RoleListDto roleDto = new RoleListDto();
        roleDto.setId(role.getId());
        roleDto.setParentListId(role.getParentListId());
        roleDto.setLabel(role.getLabel());
        roleDto.setSequenceNo(role.getSequenceNo());
        roleDto.setActive(role.getIsActive());
        roleDto.setAuthorityIds(role.getAuthorities().stream().map(AuthorityList::getId).collect(Collectors.toList()));
        return roleDto;
    }

    private RoleList convertToEntity(RoleListDto roleDto) {
        RoleList role = new RoleList();
        role.setId(roleDto.getId());
        role.setParentListId(roleDto.getParentListId());
        role.setLabel(roleDto.getLabel());
        role.setSequenceNo(roleDto.getSequenceNo());
        role.setIsActive(roleDto.isActive());
        role.setAuthorities(roleDto.getAuthorityIds().stream()
                .map(id -> authorityListRepository.findById(id).orElseThrow(() -> new RuntimeException("Authority not found")))
                .collect(Collectors.toList()));
        return role;
    }
}
