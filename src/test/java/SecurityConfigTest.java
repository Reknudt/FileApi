import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pavlov.Main;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = Main.class)
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void adminEndpoint_ShouldRequireAdminRole() throws Exception {
        // Создаем тестовый JWT с ролью USER
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "test-user-id")
                .claim("scope", "USER")  // или "roles", в зависимости от вашей конфигурации
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        // Создаем аутентификацию с USER ролью
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);

        // Выполняем запрос с аутентификацией
        mockMvc.perform(get("/api/file")
                        .with(authentication(authentication)))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminEndpoint_ShouldAllowAdminRole() throws Exception {
        // Создаем тестовый JWT с ролью ADMIN
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("sub", "admin-user-id")
                .claim("scope", "ADMIN")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(300))
                .build();

        // Создаем аутентификацию с ADMIN ролью
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );
        JwtAuthenticationToken authentication = new JwtAuthenticationToken(jwt, authorities);

        // Выполняем запрос с аутентификацией
        mockMvc.perform(get("/api/file")
                        .with(authentication(authentication)))
                .andExpect(status().isOk());
    }
}