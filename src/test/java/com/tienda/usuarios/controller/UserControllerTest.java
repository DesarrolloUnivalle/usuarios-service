package com.tienda.usuarios.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.tienda.usuarios.dto.UserResponseDTO;
import com.tienda.usuarios.model.Role;
import com.tienda.usuarios.model.User;
import com.tienda.usuarios.security.CustomUserDetails;
import com.tienda.usuarios.security.JwtUtil;
import com.tienda.usuarios.service.UserService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

@SpringBootTest
@AutoConfigureMockMvc

class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    
    @Test
    @WithMockUser(username = "juan@example.com", roles = {"CLIENTE"})
    void getUserById_deberiaRetornarUsuario() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Juan", "juan@example.com", "CLIENTE");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.email").value("juan@example.com"))
                .andExpect(jsonPath("$.rol").value("CLIENTE"));
    }

    @Test
    void getUserByIdInternal_deberiaRetornarUsuario() throws Exception {
        UserResponseDTO user = new UserResponseDTO(1L, "Ana", "ana@example.com", "ADMIN");

        Mockito.when(userService.getUserById(1L)).thenReturn(user);

        mockMvc.perform(get("/usuarios/1/internal"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana"));
    }

    @Test
    @WithMockUser(username = "juan@example.com", roles = {"CLIENTE"})
    void ping_deberiaRetornarMensaje() throws Exception {
        mockMvc.perform(get("/usuarios/ping"))
                .andExpect(status().isOk())
                .andExpect(content().string("¡El servicio de usuarios está funcionando! 🚀"));
    }

    @Test
    @WithMockUser(username = "juan@example.com", roles = {"CLIENTE"})
    void getAllUsers_deberiaRetornarListaUsuarios() throws Exception {
        Role role = new Role(1, "CLIENTE");
        User user = new User(1L, "Juan", "juan@example.com", "1234", role);

        Mockito.when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"))
                .andExpect(jsonPath("$[0].email").value("juan@example.com"));
    }

    @Test
    @WithMockUser(username = "juan@example.com", roles = {"CLIENTE"})
    void getUserByEmail_deberiaRetornarUsuario() throws Exception {
        UserResponseDTO user = new UserResponseDTO(2L, "Laura", "laura@example.com", "CLIENTE");

        Mockito.when(userService.getUserByEmail("laura@example.com")).thenReturn(user);

        mockMvc.perform(get("/usuarios/email/laura@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Laura"));
    }

    @Test
    void deleteUser_adminPuedeEliminarCualquierUsuario() throws Exception {
        doNothing().when(userService).deleteUser(5L);

        Role adminRole = new Role(1, "ADMIN");
        User admin = new User(1L, "Admin", "admin@example.com", "1234", adminRole);
        CustomUserDetails principal = new CustomUserDetails(admin, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities()
        );

        mockMvc.perform(delete("/usuarios/5").with(authentication(auth)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_usuarioPuedeEliminarseASiMismo() throws Exception {
        doNothing().when(userService).deleteUser(10L);

        Role cliente = new Role(2, "CLIENTE");
        User user = new User(10L, "Yo", "yo@example.com", "1234", cliente);
        CustomUserDetails principal = new CustomUserDetails(
            user, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities()
        );

        mockMvc.perform(delete("/usuarios/10").with(authentication(auth)))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_usuarioNoPuedeEliminarOtroUsuario() throws Exception {
        Role cliente = new Role(2, "CLIENTE");
        User user = new User(20L, "Otro", "otro@example.com", "1234", cliente);
        CustomUserDetails principal = new CustomUserDetails(
            user, List.of(new SimpleGrantedAuthority("ROLE_CLIENTE"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(
            principal, principal.getPassword(), principal.getAuthorities()
        );

        // El usuario 20 intenta eliminar al 30
        mockMvc.perform(delete("/usuarios/30").with(authentication(auth)))
                .andExpect(status().isForbidden());
    }


}
