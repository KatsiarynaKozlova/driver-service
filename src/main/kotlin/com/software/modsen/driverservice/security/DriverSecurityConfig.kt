package com.software.modsen.driverservice.security

import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
class DriverSecurityConfig (
    private val jwtAuthenticationConverterPassenger: JwtAuthenticationConverterDriver
){
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .oauth2ResourceServer { oauth: OAuth2ResourceServerConfigurer<HttpSecurity> ->
                oauth.jwt { jwt ->
                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())
                }
            }
            .authorizeHttpRequests { request ->
                request
                    .requestMatchers(HttpMethod.GET, "/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
        return http.build()
    }

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(jwtAuthenticationConverterPassenger)
        return converter
    }
}
