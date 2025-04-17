package org.pavlov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SpringSecurityConfiguration {

    interface AuthoritiesConverter extends Converter<Map<String, Object>, Collection<GrantedAuthority>> {}

    @Bean
    AuthoritiesConverter realmRolesAuthoritiesConverter() {
        return claims -> {
            final var resourceAccess = Optional.ofNullable((Map<String, Object>) claims.get("resource_access"));
            final var client_content =
                    resourceAccess.flatMap(map -> Optional.ofNullable((Map<String, Object>) map.get("example-client")));
            final var roles =
                    client_content.flatMap(map -> Optional.ofNullable((List<String>) map.get("roles")));
            return roles.map(List::stream).orElse(Stream.empty()).map(SimpleGrantedAuthority::new)
                    .map(GrantedAuthority.class::cast).toList();
        };
    }

    @Bean
    JwtAuthenticationConverter authenticationConverter(
            Converter<Map<String, Object>, Collection<GrantedAuthority>> authoritiesConverter) {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter
                .setJwtGrantedAuthoritiesConverter(jwt -> authoritiesConverter.convert(jwt.getClaims()));
        return jwtAuthenticationConverter;
    }

    @Bean
    SecurityFilterChain resourceServerSecurityFilterChain(HttpSecurity http,
                                                          Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter) throws Exception {
        http.oauth2ResourceServer(resourceServer -> {
            resourceServer.jwt(jwtDecoder -> {
                jwtDecoder.jwtAuthenticationConverter(jwtAuthenticationConverter);
            });
        });

        http.sessionManagement(sessions -> {
            sessions.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        }).csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(requests -> {
//            requests.requestMatchers("/api/v1/employee/**").hasAnyAuthority("user", "admin");
//            requests.requestMatchers("/api/v1/tasks/**").hasAuthority("admin");
//            requests.requestMatchers("/api/v1/employees/**").hasAuthority("user");
//            requests.requestMatchers("/api/v1/employees/**").access(AuthorizationManagers
//                    .allOf(AuthorityAuthorizationManager.hasAuthority("user"), AuthorityAuthorizationManager.hasAuthority("admin")));
//            requests.requestMatchers("/api/v1/**").authenticated();
            requests.anyRequest().permitAll();
        });

        return http.build();
    }

}