package com.facilon.app.service;

import com.facilon.app.dto.AuthorityListDto;
import com.facilon.app.model.AuthorityList;
import com.facilon.app.repository.AuthorityListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuthorityListService {

    @Autowired
    private AuthorityListRepository authorityListRepository;

    public List<AuthorityListDto> getAllAuthorities() {
        return authorityListRepository.findAll().stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public Optional<AuthorityListDto> getAuthorityById(Long id) {
        return authorityListRepository.findById(id).map(this::convertToDto);
    }

    public AuthorityListDto saveAuthority(AuthorityListDto authorityDto) {
        AuthorityList authority = convertToEntity(authorityDto);
        AuthorityList savedAuthority = authorityListRepository.save(authority);
        return convertToDto(savedAuthority);
    }

    public void deleteAuthority(Long id) {
        authorityListRepository.deleteById(id);
    }

    private AuthorityListDto convertToDto(AuthorityList authority) {
        AuthorityListDto authorityDto = new AuthorityListDto();
        authorityDto.setId(authority.getId());
        authorityDto.setAuthorityName(authority.getAuthorityName());
        authorityDto.setDescription(authority.getDescription());
        return authorityDto;
    }

    private AuthorityList convertToEntity(AuthorityListDto authorityDto) {
        AuthorityList authority = new AuthorityList();
        authority.setId(authorityDto.getId());
        authority.setAuthorityName(authorityDto.getAuthorityName());
        authority.setDescription(authorityDto.getDescription());
        return authority;
    }
}
