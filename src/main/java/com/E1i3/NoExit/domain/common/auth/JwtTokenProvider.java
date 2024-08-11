package com.E1i3.NoExit.domain.common.auth;

import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenProvider {
	@Value("${jwt.secretKey}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private int expiration;

	public String createToken(String email, String role) {
		// claims는 사용자 정보(payload 정보)
		Claims claims = Jwts.claims().setSubject(email);
		claims.put("role", role);
		Date now =  new Date();
		String token = Jwts.builder()
			.setClaims(claims)
			.setIssuedAt(now)    //생성시간
			.setExpiration(new Date(now.getTime() + (expiration * 60 * 1000L)))  // 만료시간 30분
			.signWith(SignatureAlgorithm.HS256, secretKey)
			.compact();
		return token;
	}
}