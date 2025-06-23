package com.tienda.usuarios.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test-error")
public class DummyErrorController {

    @GetMapping("/illegal-arg")
    public void throwIllegalArgument() {
        throw new IllegalArgumentException("Dato inválido");
    }

    @GetMapping("/runtime")
    public void throwRuntimeException() {
        throw new RuntimeException("No se encontró el recurso");
    }

    @GetMapping("/bad-credentials")
    public void throwBadCredentials() {
        throw new org.springframework.security.authentication.BadCredentialsException("Credenciales inválidas");
    }

    @GetMapping("/user-not-found")
    public void throwUserNotFound() {
        throw new org.springframework.security.core.userdetails.UsernameNotFoundException("Usuario no encontrado");
    }
}
