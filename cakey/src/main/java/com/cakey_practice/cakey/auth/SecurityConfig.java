package com.cakey_practice.cakey.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationFilter authenticationFilter;
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomJwtAuthenticationEntryPoint customJwtAuthenticationEntryPoint;

    private static final String[] AUTH_WHITELIST = {
            "/actuator/health",
            "/v1/user/login",
            "/token-refresh"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(
                        exception -> exception.authenticationEntryPoint(customJwtAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_WHITELIST).permitAll() // 화이트리스트 경로 허용
                        .requestMatchers("/v1/user/test1").authenticated() // test1에만 jwtAuthenticationFilter 적용
                        .requestMatchers("/v1/user/test2").authenticated() // test2에만 customAuthenticationFilter 적용
                        .anyRequest().authenticated())
                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(customAuthenticationFilter, authenticationFilter.getClass());
        return http.build();
    }

//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http.csrf(AbstractHttpConfigurer::disable)
//                .formLogin(AbstractHttpConfigurer::disable)
//                .httpBasic(AbstractHttpConfigurer::disable)
//                .sessionManagement(
//                        session -> {
//                            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
//                        }
//                )
//                .exceptionHandling(
//                        exceptionHandlingConfigurer -> exceptionHandlingConfigurer.authenticationEntryPoint(customJwtAuthenticationEntryPoint))
//                .authorizeHttpRequests(
//                        authorizationManagerRequestMatcherRegistry ->
//                                authorizationManagerRequestMatcherRegistry
//                                        .requestMatchers(AUTH_WHITELIST).permitAll()
//                                        .anyRequest()
//                                        .authenticated())
//                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//        return http.build();
//    }
//    @Bean
//    public FilterRegistrationBean<AuthenticationFilter> jwtAuthenticationFilterRegistration() {
//        FilterRegistrationBean<AuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
//        registrationBean.setFilter(jwtAuthenticationFilter);
//        registrationBean.addUrlPatterns("/v1/user/test1"); // /test1 경로에만 jwtAuthenticationFilter 적용
//        registrationBean.setOrder(1); // 필터 순서 설정
//        return registrationBean;
//    }

    @Bean
    public FilterRegistrationBean<CustomAuthenticationFilter> customAuthenticationFilterRegistration() {
        FilterRegistrationBean<CustomAuthenticationFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(customAuthenticationFilter);
        registrationBean.addUrlPatterns("/v1/user/test2"); // /test2 경로에만 customAuthenticationFilter 적용
        registrationBean.setOrder(2); // 필터 순서 설정
        return registrationBean;
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(AUTH_WHITELIST);
    }
}
