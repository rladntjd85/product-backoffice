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
                                "/css/**", "/js/**", "/static/img/**", "/uploads/**"
                        ).permitAll()

                        .requestMatchers("/auth/password-change").authenticated()

                        // MD 허용(백오피스 일부)
                        .requestMatchers("/admin", "/admin").hasAnyRole("ADMIN", "MD")
                        .requestMatchers("/admin/products/**").hasAnyRole("ADMIN", "MD")
                        .requestMatchers("/admin/categories/**").hasAnyRole("ADMIN", "MD")

                        .requestMatchers("/admin/users/**").hasRole("ADMIN") // 사용자 운영은 ADMIN만
                        .requestMatchers("/admin/audit/**").hasRole("ADMIN") // 사용자 운영은 ADMIN만

                        // 상품 KPI/재고 알림은 ADMIN, MD 모두
                        .requestMatchers("/admin/api/dashboard/summary").hasAnyRole("ADMIN", "MD")
                        .requestMatchers("/admin/api/dashboard/stock-alerts").hasAnyRole("ADMIN", "MD")

                        // 감사 관련은 ADMIN만
                        .requestMatchers("/admin/api/dashboard/audit/**").hasRole("ADMIN")
                        .requestMatchers("/admin/api/dashboard/recent-audits").hasRole("ADMIN")

                        // 추가: 카테고리 조회 API도 MD에게 허용
                        .requestMatchers("/admin/api/categories/**").hasAnyRole("ADMIN", "MD")
                        // 추가: 상품 등록 시 이미지 업로드 API도 MD에게 허용 (Summernote용)
                        .requestMatchers("/admin/api/image/**").hasAnyRole("ADMIN", "MD")
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

                            // 1) CSRF 토큰 불일치(업로드 초과/요청 깨짐 등)면 폼으로 되돌리기
                            if (ex2 instanceof org.springframework.security.web.csrf.InvalidCsrfTokenException) {

                                String uri = request.getRequestURI(); // /admin/products 또는 /admin/products/{id}
                                String msg = "파일 용량은 최대 5MB입니다. 다시 시도하세요.";

                                // 등록 POST(/admin/products) → /new로
                                if ("/admin/products".equals(uri)) {
                                    response.sendRedirect("/admin/products/new?error=" + java.net.URLEncoder.encode(msg, "UTF-8"));
                                    return;
                                }

                                // 수정 POST(/admin/products/{id}) → /{id}/edit로
                                if (uri.matches("^/admin/products/\\d+$")) {
                                    String id = uri.substring(uri.lastIndexOf("/") + 1);
                                    response.sendRedirect("/admin/products/" + id + "/edit?error=" + java.net.URLEncoder.encode(msg, "UTF-8"));
                                    return;
                                }

                                response.sendRedirect("/admin/products?error=" + java.net.URLEncoder.encode(msg, "UTF-8"));
                                return;
                            }

                            // 2) 그 외 권한 부족은 403
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
