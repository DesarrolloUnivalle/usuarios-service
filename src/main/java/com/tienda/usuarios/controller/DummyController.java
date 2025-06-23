package com.tienda.usuarios.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DummyController {

    @GetMapping("/dummy/secure")
    public String secureEndpoint() {
        return "Acceso autenticado exitoso";
    }
}
