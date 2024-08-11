package com.E1i3.NoExit.domain.common.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.E1i3.NoExit.domain.common.auth.JwtAuthFilter;

@Configuration
@EnableWebSecurity	// security 관련한 코드
@EnableGlobalMethodSecurity(prePostEnabled = true)	// pre: 사전 검증, post: 사후 검증
public class SecurityConfigs {

	@Autowired
	private JwtAuthFilter jwtAuthFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity

				.csrf().disable()
				.cors().and() // CORS 활성화
				.httpBasic().disable()
				.authorizeRequests()
				.antMatchers(
						"/email/requestCode",
						"/",
					// 	게시글, 후기 조회하는 페이지는 모두 로그인하지 않아도 가능하도록
						"/doLogin","/owner/create",
						// 김민성 : Swagger 관련 경로를 허용 , 접속 경로 : http://localhost:8080/swagger-ui/#/
						"/member/create", "/swagger-ui/**",
						"/review/all",
						"/swagger-resources/**",
						"/swagger-ui.html",
						"/v2/api-docs",
						"/webjars/**",
						//웹소켓 test 403 해결
						"/ws/chat/**",
						"/chat/**"
				).permitAll()
				.antMatchers("/reservation/create").hasRole("USER")
				.anyRequest().authenticated()
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
				.build();
	}
}