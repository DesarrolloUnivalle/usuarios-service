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

@WebMvcTest(RepartidorController.class)
@AutoConfigureMockMvc(addFilters = false)
class RepartidorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtUtil jwtUtil;

    @Test
    void accesoRepartidor_deberiaRetornarMensaje() throws Exception {
        mockMvc.perform(get("/repartidor/acceso"))
            .andExpect(status().isOk())
            .andExpect(content().string("Hola, repartidor. Puedes ver esta ruta."));
    }
}
