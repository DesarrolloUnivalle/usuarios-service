package com.tienda.usuarios.service;

import com.tienda.usuarios.dto.AuthRequestDTO;
import com.tienda.usuarios.dto.AuthResponseDTO;
import com.tienda.usuarios.model.User;
import com.tienda.usuarios.repository.UserRepository;
import com.tienda.usuarios.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private AuthRequestDTO request;
    private User user;

    @BeforeEach
    void setUp() {
        request = new AuthRequestDTO("test@example.com", "password123");
        user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
    }

    @Test
    void autenticarUsuario_usuarioYPasswordCorrectos_retornaToken() {
        // Arrange
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("test@example.com");
        when(jwtUtil.generateToken("test@example.com")).thenReturn("jwt-token");

        // Act
        AuthResponseDTO response = authService.autenticarUsuario(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt-token", response.token());
    }

    @Test
    void autenticarUsuario_usuarioNoExiste_lanzaExcepcion() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.autenticarUsuario(request));

        assertEquals("Usuario no encontrado", exception.getMessage());
    }

    @Test
    void autenticarUsuario_passwordIncorrecta_lanzaExcepcion() {
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.autenticarUsuario(request));

        assertEquals("Contraseña incorrecta", exception.getMessage());
    }
}
