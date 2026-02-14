package com.rladntjd85.backoffice.common.config;

import com.rladntjd85.backoffice.common.security.jwt.JwtAuthenticationFilter;
import com.rladntjd85.backoffice.common.security.jwt.JwtTokenProvider;
import com.rladntjd85.backoffice.common.security.web.AuditAuthenticationFailureHandler;
import com.rladntjd85.backoffice.common.security.web.AuditAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuditAuthenticationSuccessHandler successHandler;
    private final AuditAuthenticationFailureHandler failureHandler;

    /**
     * API (JWT) 체인: /api/** 는 세션 없이 JWT로만 인증
     */
    @Bean
    @Order(1)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // 현재 정책: 백오피스(관리/MD)만 상품 관련 확인 가능
                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN", "MD")

                        .anyRequest().authenticated()
                )
                // JWT 사용이면 httpBasic은 굳이 필요 없음. 유지/제거는 선택.
                .httpBasic(Customizer.withDefaults());

        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    /**
     * Web (세션) 체인: Thymeleaf 화면 및 /admin/** 보호
     */
    @Bean
    @Order(2)
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/logout",
                                "/error", "/error/**",
                                "/css/**", "/js/**", "/img/**", "/uploads/**"
                        ).permitAll()

                        .requestMatchers("/auth/password-change").authenticated()

                        // MD 허용(백오피스 일부)
                        .requestMatchers("/admin", "/admin").hasAnyRole("ADMIN", "MD")
                        .requestMatchers("/admin/products/**").hasAnyRole("ADMIN", "MD")
                        .requestMatchers("/admin/categories/**").hasAnyRole("ADMIN", "MD")
                        .requestMatchers("/admin/users/**").hasRole("ADMIN") // 사용자 운영은 ADMIN만
                        .requestMatchers("/admin/audit/**").hasRole("ADMIN") // 사용자 운영은 ADMIN만

                        // /admin 대시보드는 ADMIN만
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 기타 admin 페이지는 기본 ADMIN (안전망)
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, ex2) -> {
                            // 콘솔 로그
                            System.out.println("[ACCESS DENIED] uri=" + request.getRequestURI()
                                    + " user=" + (request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "anonymous"));
                            // 에러 페이지로 이동(템플릿 만들어야 함)
                            response.sendRedirect("/error/403");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                )
                // Thymeleaf form 사용 시 CSRF는 켜는 게 기본(유지)
                .csrf(Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
