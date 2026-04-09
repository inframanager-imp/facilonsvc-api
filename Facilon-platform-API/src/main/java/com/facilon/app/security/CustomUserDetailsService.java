package com.facilon.app.security;


import com.facilon.app.model.UserGroup;
import com.facilon.app.repository.RoleListRepository;
import com.facilon.app.model.AuthorityList;
import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.model.RoleList;
import com.facilon.app.service.UserService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;
	@Autowired
	private RoleListRepository roleListRepository;

	@Value("${app.security.passwordExpiryInDays}")
	private Integer expiryDays;

	@Value("${app.security.maxLoginFailureCount}")
	private Integer maxFailureCount;


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String loginIdOrEmail) throws UsernameNotFoundException {
		log.info("In UserDetails loadUserByUsername " + loginIdOrEmail);
		List<String> groupNames = new ArrayList<>();
		AuthorizedUser user = userService.findByEmailOrLoginId(loginIdOrEmail).orElseThrow(
				() -> new UsernameNotFoundException("User not found with username or email : " + loginIdOrEmail));
		groupNames=user.getUserGroups().stream().map(UserGroup::getGroupName).collect(Collectors.toSet()).stream().toList();;
		List<String> authorityNames = user.getUserGroups().stream().map(UserGroup::getRoles).flatMap(roleLists -> roleLists.stream()).flatMap(role -> role.getAuthorities().stream())
				.map(AuthorityList::getAuthorityName)
				.collect(Collectors.toSet()).stream().toList();

		List<String> roles=user.getUserGroups().stream().map(UserGroup::getRoles).flatMap(roleLists -> roleLists.stream()).map(RoleList::getLabel).collect(Collectors.toSet()).stream().toList();
		return UserPrincipal.create(user ,expiryDays,maxFailureCount,authorityNames,groupNames,roles);
	}

	@Transactional
	public UserDetails loadUserById(Long userId) throws UsernameNotFoundException {
		log.info("In UserDetails loadUserByUsername " + userId);
		// Let people login with either loginId or email
		List<String> groupNames = new ArrayList<>();
		AuthorizedUser user = userService.findById(userId);
		groupNames=user.getUserGroups().stream().map(UserGroup::getGroupName).collect(Collectors.toSet()).stream().toList();
		List<String> authorityNames = user.getUserGroups().stream().map(UserGroup::getRoles).flatMap(roleLists -> roleLists.stream()).flatMap(role -> role.getAuthorities().stream())
				.map(AuthorityList::getAuthorityName)
				.collect(Collectors.toSet()).stream().toList();

		List<String> roles=user.getUserGroups().stream().map(UserGroup::getRoles).flatMap(roleLists -> roleLists.stream()).map(RoleList::getLabel).collect(Collectors.toSet()).stream().toList();
		return UserPrincipal.create(user, expiryDays, maxFailureCount,authorityNames,groupNames,roles);

	}
	@Transactional
	public UserDetails loadUserEmail(String email) throws UsernameNotFoundException {
		log.info("In UserDetails loadUser email " + email);
		List<String> groupNames = new ArrayList<>();
		AuthorizedUser user = userService.findByEmail(email);
		groupNames=user.getUserGroups().stream().map(UserGroup::getGroupName).collect(Collectors.toSet()).stream().toList();;
		List<String> authorityNames = user.getUserGroups().stream().map(UserGroup::getRoles).flatMap(roleLists -> roleLists.stream()).flatMap(role -> role.getAuthorities().stream())
				.map(AuthorityList::getAuthorityName)
				.collect(Collectors.toSet()).stream().toList();

		List<String> roles=user.getUserGroups().stream().map(UserGroup::getRoles).flatMap(roleLists -> roleLists.stream()).map(RoleList::getLabel).collect(Collectors.toSet()).stream().toList();
		return UserPrincipal.create(user, expiryDays, maxFailureCount,authorityNames,groupNames,roles);

	}

	}
