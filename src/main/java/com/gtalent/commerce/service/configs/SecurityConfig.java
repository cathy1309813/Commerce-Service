package com.gtalent.commerce.service.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity  //啟用 Spring Security 的 Web 安全功能，告訴 Spring Boot 這是一個 Web Security 設定類。
public class SecurityConfig {
    //定義安全性設定檔 (那些urls需要被保護...)
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())  //CSRF是防止跨站請求偽造攻擊 (Cross-Site Request Forgery)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/jwt/**").permitAll()  //登入/註冊 API 不需要驗證
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**","/swagger-ui.html").permitAll()
                        .anyRequest().authenticated()                     //其他 API 都要 JWT
                )
                //每個請求進來時，先讓 jwtAuthFilter 驗證 JWT，確認合法後再往下傳給 Spring Security 的其他 Filter
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
