package com.tienda.usuarios.security;

import com.tienda.usuarios.config.SecurityConfig;
import com.tienda.usuarios.controller.DummyController;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers =DummyController.class)
@Import({JwtAuthFilter.class, SecurityConfig.class})
class JwtAuthFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @RestController
    @RequestMapping("/dummy")
    static class DummyController {
        @GetMapping("/secure")
        public String secureEndpoint() {
            return "OK";
        }
    }

    @Test
    void noAuthorizationHeader_shouldPassThrough() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dummy/secure"))
                .andExpect(status().isForbidden()); // No auth context set
    }

    @Test
    void malformedToken_shouldNotAuthenticate() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dummy/secure")
                        .header(HttpHeaders.AUTHORIZATION, "Invalid token"))
                .andExpect(status().isForbidden());

    }

    @Test
    void validToken_shouldAuthenticate() throws Exception {
        String token = "Bearer valid-token";
        String username = "test@example.com";
        String role = "ROLE_CLIENTE";

        // Creamos usuario de dominio
        com.tienda.usuarios.model.User domainUser = new com.tienda.usuarios.model.User();
        domainUser.setId(1L);
        domainUser.setEmail(username);
        domainUser.setPassword("pass");

        // Creamos CustomUserDetails con role
        CustomUserDetails userDetails = new CustomUserDetails(domainUser,
            List.of(new SimpleGrantedAuthority(role)));

        Mockito.when(jwtUtil.extractUsername("valid-token")).thenReturn(username);
        Mockito.when(jwtUtil.extractClaim(eq("valid-token"), any())).thenReturn(role);
        Mockito.when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        Mockito.when(jwtUtil.isTokenValid("valid-token", userDetails)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.get("/dummy/secure")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());
    }


    @Test
    void invalidToken_shouldReturnUnauthorized() throws Exception {
        String token = "Bearer invalid-token";

        Mockito.when(jwtUtil.extractUsername("invalid-token")).thenThrow(new RuntimeException("Token inválido"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dummy/secure")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isUnauthorized());
    }
}