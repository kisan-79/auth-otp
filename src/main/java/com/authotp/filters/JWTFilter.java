package com.authotp.filters;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authotp.utils.JWTUtil;

@Component
public class JWTFilter extends OncePerRequestFilter {

	@Autowired
	JWTUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");
		String jwt = null;
		String identifier = null;

		if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
			jwt = authorizationHeader.substring(7);
			identifier = jwtUtil.extractIdentifier(jwt);
		}

		if (identifier != null && jwtUtil.validateToken(jwt, identifier) && SecurityContextHolder.getContext().getAuthentication() == null) {

			AuthenticationFilter apiToken = new AuthenticationFilter("abc", "xyz", AuthorityUtils.createAuthorityList());
			SecurityContextHolder.getContext().setAuthentication(apiToken);

		}

		filterChain.doFilter(request, response);

	}

}
