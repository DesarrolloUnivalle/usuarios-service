package com.tienda.usuarios.config;

import com.tienda.usuarios.security.JwtAuthFilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityConfigTest {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void contextLoads_yBeansDeSeguridadDeberianEstarDisponibles() {
        assertNotNull(jwtAuthFilter, "JwtAuthFilter no debería ser null");
        assertNotNull(passwordEncoder, "PasswordEncoder no debería ser null");
        assertNotNull(authenticationProvider, "AuthenticationProvider no debería ser null");
        assertNotNull(securityFilterChain, "SecurityFilterChain no debería ser null");
    }

    @Test
    void passwordEncoder_deberiaCodificarYVerificarCorrectamente() {
        String raw = "secreta123";
        String encoded = passwordEncoder.encode(raw);
        assertTrue(passwordEncoder.matches(raw, encoded), "El password debe coincidir después del encoding");
    }
}
