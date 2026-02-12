package pl.patryk.cryptomarketranker.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final BearerFilter bearerFilter;

    public SecurityConfiguration(RestAuthenticationEntryPoint restAuthenticationEntryPoint, BearerFilter bearerFilter) {
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.bearerFilter = bearerFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .securityContext(sc -> sc.requireExplicitSave(false))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/spread/calculate").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/spread/ranking").hasRole("USER")
                        .anyRequest().permitAll()

                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                );

        http.addFilterBefore(bearerFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}