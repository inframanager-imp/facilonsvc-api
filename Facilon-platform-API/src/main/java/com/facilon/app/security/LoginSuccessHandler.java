package com.facilon.app.security;

import com.facilon.app.model.AuthorizedUser;
import com.facilon.app.repository.AuthorizedUserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "logi")
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

	private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

	@Value("${app.client_url}")
	private String clientUrl;
  @Autowired
  private JwtTokenProvider tokenProvider;
	@Autowired
	private AuthorizedUserRepository authorizedUserCrud;

	@Autowired
	CustomUserDetailsService customUserDetailsService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException {
		log.info("login successful for " + authentication.getName());
		handle(request, response, authentication);
		clearAuthenticationAttributes(request);
	}

	protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException {
		AuthorizedUser user = null;
		if ((authentication.getPrincipal() instanceof UserPrincipal)) {
			UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
			user = authorizedUserCrud.findById(userPrincipal.getId()).get();
		} else
			if (authentication instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
			DefaultOidcUser oidcUser = (DefaultOidcUser) oauth2Token.getPrincipal();

			String email = oidcUser.getEmail();
			user = authorizedUserCrud.findByEmailId(email).orElseGet(() -> {
				// Create a new user if not found
				AuthorizedUser newUser = new AuthorizedUser();
				newUser.setEmailId(email);
				newUser.setLoginId(email);
				// Set other fields as necessary
				return authorizedUserCrud.save(newUser);
			});

			authorizeUser(user);
		}
		//authorizeUser(user);
        UserDetails userDetails= customUserDetailsService.loadUserEmail(user.getEmailId());
		UsernamePasswordAuthenticationToken authentication2 = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());
		String token = tokenProvider.generateToken(authentication2);
		response.addHeader("Authorization", "Bearer " + token);

		String targetUrl = clientUrl+"/dashboard?token=" + token;

		log.info("target url " + targetUrl);

		if (response.isCommitted()) {
			log.debug("Response has already been committed. Unable to redirect to " + targetUrl);
			return;
		}

		redirectStrategy.sendRedirect(request, response, targetUrl);
	}

	protected void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return;
		}
		session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
	}



	private void authorizeUser(AuthorizedUser authorizedUser) {
		if (!(authorizedUser == null)) {
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();

			List<GrantedAuthority> updatedAuthorities = new ArrayList<>(auth.getAuthorities());
			/*authorizedUser.getRoleLists().stream().forEach(roleList -> {

				roleList.getAuthorities().stream().map(authorityList -> {
					return updatedAuthorities.add(new SimpleGrantedAuthority(authorityList.getAuthorityName() ));
				});
			});*/
			Authentication newAuth = new UsernamePasswordAuthenticationToken(auth.getPrincipal(), auth.getCredentials(),
					updatedAuthorities);

			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
	}

	public Authentication authorize(Authentication authentication) {
		AuthorizedUser user = null;
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		user = authorizedUserCrud.findById(userPrincipal.getId()).get();
		List<GrantedAuthority> authorities =new ArrayList<>();
	/*	user.getRoleLists().stream().forEach(roleList -> {

			roleList.getAuthorities().stream().map(authorityList -> {
				return authorities.add(new SimpleGrantedAuthority(authorityList.getAuthorityName()));
			});
		});*/
		Authentication newAuth = null;
		if (!(userPrincipal == null)) {
			newAuth = new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(newAuth);
		}
		return newAuth;
	}


}
