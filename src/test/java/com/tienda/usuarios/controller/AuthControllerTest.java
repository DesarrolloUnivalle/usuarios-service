package com.tienda.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tienda.usuarios.dto.UserRequestDTO;
import com.tienda.usuarios.dto.UserResponseDTO;
import com.tienda.usuarios.exception.GlobalExceptionHandler;
import com.tienda.usuarios.model.Role;
import com.tienda.usuarios.security.JwtUtil;
import com.tienda.usuarios.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_credencialesValidas_deberiaRetornarToken() throws Exception {
        String email = "juan@example.com";
        String password = "1234";
        String token = "mocked-jwt";

        UserDetails userDetails = new org.springframework.security.core.userdetails.User(email, password, Collections.emptyList());

        Mockito.when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);
        Mockito.when(jwtUtil.generateToken(userDetails)).thenReturn(token);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(token));
    }

    @Test
    void login_credencialesInvalidas_deberiaRetornar401() throws Exception {
        String email = "juan@example.com";
        String password = "wrongpass";

        Mockito.doThrow(new BadCredentialsException("Credenciales incorrectas"))
            .when(authenticationManager)
            .authenticate(any());


        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales incorrectas"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    void register_usuarioValido_deberiaRetornar201() throws Exception {
        Role role = new Role();
        role.setId(1);
        role.setNombre("CLIENTE");

        UserRequestDTO request = new UserRequestDTO("Juan", "juan@example.com", "1234", role);
        UserResponseDTO response = new UserResponseDTO(1L, "Juan", "juan@example.com", "CLIENTE");

        Mockito.when(userService.registrarUsuario(any())).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void register_emailDuplicado_deberiaRetornar400() throws Exception {
        
        Role role = new Role();
        role.setId(1);
        role.setNombre("CLIENTE");

         UserRequestDTO request = new UserRequestDTO("Juan", "repetido@example.com", "1234", role);

        when(userService.registrarUsuario(any()))
            .thenThrow(new IllegalArgumentException("El correo ya está registrado"));

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("El correo ya está registrado"))
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void login_usuarioNoExiste_deberiaRetornar404YMensajeJson() throws Exception {
        String email = "noexiste@example.com";

        when(userDetailsService.loadUserByUsername(email))
            .thenThrow(new UsernameNotFoundException("Usuario no encontrado"));


        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", "1234");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(credentials)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario no encontrado"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.timestamp").exists());

    }

    @Test
    void register_emailExistente_deberiaRetornar400YMensajeJson() throws Exception {
    Role role = new Role();
    role.setId(1);
    role.setNombre("CLIENTE");

    UserRequestDTO requestDTO = new UserRequestDTO("Juan", "repetido@example.com", "1234", role);

    when(userService.registrarUsuario(any())).thenThrow(new IllegalArgumentException("El correo ya está registrado"));

    mockMvc.perform(post("/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(new ObjectMapper().writeValueAsString(requestDTO)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("El correo ya está registrado"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.error").value("Bad Request"))
        .andExpect(jsonPath("$.timestamp").exists());
    }


}
