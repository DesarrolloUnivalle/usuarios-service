package com.tienda.usuarios.security;

import io.jsonwebtoken.ExpiredJwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    // Valores simulados
    private final String secretKey = "ZmFrZV9zZWNyZXRfZm9yX3Rlc3Rfb25seV9ub3RfcmVhbA=="; // base64 de "fake_secret_for_test_only_not_real"
    private final long expirationMs = 1000 * 60 * 60; // 1 hora
    private final String issuer = "mi-app";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Inyectamos los valores simulando el @Value
        java.lang.reflect.Field secretField = null;
        java.lang.reflect.Field expirationField = null;
        java.lang.reflect.Field issuerField = null;

        try {
            secretField = JwtUtil.class.getDeclaredField("secret");
            expirationField = JwtUtil.class.getDeclaredField("expiration");
            issuerField = JwtUtil.class.getDeclaredField("issuer");

            secretField.setAccessible(true);
            expirationField.setAccessible(true);
            issuerField.setAccessible(true);

            secretField.set(jwtUtil, secretKey);
            expirationField.set(jwtUtil, expirationMs);
            issuerField.set(jwtUtil, issuer);
        } catch (Exception e) {
            throw new RuntimeException("Error al inyectar dependencias de prueba", e);
        }
    }

    @Test
    void generateToken_y_validarToken_deberiaFuncionarCorrectamente() {
        var userDetails = new User("testuser@example.com", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token, userDetails));
    }

    @Test
    void extractUsername_deberiaRetornarElUsuarioCorrecto() {
        var userDetails = new User("testuser@example.com", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals("testuser@example.com", extractedUsername);
    }

    @Test
    void extractExpiration_deberiaRetornarFechaValida() {
        var userDetails = new User("testuser@example.com", "password", Collections.emptyList());

        String token = jwtUtil.generateToken(userDetails);

        Date expirationDate = jwtUtil.extractExpiration(token);
        assertTrue(expirationDate.after(new Date()));
    }

    @Test
    void tokenExpirado_deberiaSerInvalido() throws Exception {
        var userDetails = new User("expireduser@example.com", "password", Collections.emptyList());

        // Crear instancia y setear campos por reflexión
        var expiredUtil = new JwtUtil();
        setField(expiredUtil, "secret", secretKey);
        setField(expiredUtil, "expiration", -3600000L); // expirado hace 1 hora
        setField(expiredUtil, "issuer", issuer);

        String expiredToken = expiredUtil.generateToken(userDetails);

        // Capturar la excepción porque está completamente expirado
        assertThrows(ExpiredJwtException.class, () -> expiredUtil.isTokenValid(expiredToken, userDetails));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        var field = JwtUtil.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

}
