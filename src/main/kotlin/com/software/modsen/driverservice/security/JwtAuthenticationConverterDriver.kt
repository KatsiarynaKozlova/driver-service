package com.software.modsen.driverservice.security

import com.software.modsen.driverservice.util.SecurityConstants
import org.springframework.context.annotation.Configuration
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.core.convert.converter.Converter

import java.util.stream.Collectors

@Configuration
class JwtAuthenticationConverterDriver : Converter<Jwt, Collection<GrantedAuthority>> {
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val resourceAccess = jwt.getClaim<Map<String, List<String>>>(SecurityConstants.REALM_ACCESS)
        val resourceRoles: List<String>? = resourceAccess?.get(SecurityConstants.ROLES)

        if (resourceAccess == null || resourceRoles == null) {
            return setOf()
        }

        return resourceRoles.stream()
            .map { role -> SimpleGrantedAuthority(SecurityConstants.PREFIX_ROLE + role) }
            .collect(Collectors.toList())
    }
}
