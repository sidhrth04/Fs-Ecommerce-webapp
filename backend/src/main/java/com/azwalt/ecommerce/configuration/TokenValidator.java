package com.azwalt.ecommerce.configuration;

import java.io.IOException;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TokenValidator extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
			throws ServletException, IOException {
		String token = request.getHeader("Authorization");
		if (token != null) {
			try {
				token = token.substring(7);
				SecretKey key = Keys.hmacShaKeyFor(TokenConstants.SECRET_KEY.getBytes());
				Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
				String username = String.valueOf(claims.get("username"));
				String authorities = String.valueOf(claims.get("authorities"));
				List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
				Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, auths);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch (Exception exception) {
				throw new BadCredentialsException("Invalid Token.");
			}
		}
		filterChain.doFilter(request, response);
	}

}