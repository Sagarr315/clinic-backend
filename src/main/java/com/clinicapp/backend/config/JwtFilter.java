package com.clinicapp.backend.config;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.clinicapp.backend.services.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String header = request.getHeader("Authorization");
		String token = null;
		String username = null;

		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7);
			username = jwtUtils.getUsername(token);
		}

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);

			if (jwtUtils.validateToken(token)) {
				// SECURITY FIX: Validate clinic access
				if (!hasAccessToClinic(request, token)) {
					response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					response.getWriter().write("Access denied to this clinic");
					return;
				}

				UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
						null, userDetails.getAuthorities());
				authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}

		chain.doFilter(request, response);
	}

	private boolean hasAccessToClinic(HttpServletRequest request, String token) {
		// Super Admin can access everything
		if ("ROLE_SUPERADMIN".equals(jwtUtils.getRole(token))) {
			return true;
		}

		// Extract clinicId from URL path
		Long requestedClinicId = extractClinicIdFromPath(request.getRequestURI());
		Long tokenClinicId = jwtUtils.getClinicId(token);

		// If no clinic in path, allow access
		if (requestedClinicId == null) {
			return true;
		}

		// Validate user has access to requested clinic
		return requestedClinicId.equals(tokenClinicId);
	}

	private Long extractClinicIdFromPath(String path) {
		Pattern pattern = Pattern.compile("/clinic/(\\d+)");
		Matcher matcher = pattern.matcher(path);
		if (matcher.find()) {
			return Long.parseLong(matcher.group(1));
		}
		return null;
	}
}