package com.tienda.usuarios.controller;

import com.tienda.usuarios.security.JwtUtil;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HomeController.class)
@AutoConfigureMockMvc(addFilters = false)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void home_deberiaRetornarMensaje() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(content().string("Bienvenido al servicio de gestión de usuarios 🚀"));
    }

    @Test
    void inicio_deberiaRetornarMensaje() throws Exception {
        mockMvc.perform(get("/inicio"))
            .andExpect(status().isOk())
            .andExpect(content().string("¡El servicio de usuarios está funcionando! 🚀"));
    }

    @Test
    void publicEndpoint_deberiaRetornarMensaje() throws Exception {
        mockMvc.perform(get("/public"))
            .andExpect(status().isOk())
            .andExpect(content().string("Este endpoint es público."));
    }

    @Test
    void adminEndpoint_deberiaRetornarMensaje() throws Exception {
        mockMvc.perform(get("/admin"))
            .andExpect(status().isOk())
            .andExpect(content().string("Bienvenido, administrador."));
    }
}
