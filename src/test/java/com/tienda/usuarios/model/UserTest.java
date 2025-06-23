package com.tienda.usuarios.model;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void usuarioValido_debePasarValidacion() {
        User user = new User();
        user.setNombre("Juan");
        user.setEmail("juan@email.com");
        user.setPassword("securepass123");
        Role role = new Role();
        role.setId(1);
        role.setNombre("USER");
        user.setRol(role);

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty(), "El usuario válido no debe tener errores de validación");
    }

    @Test
    void usuarioInvalido_debeFallarValidacion() {
        User user = new User(); // campos vacíos

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")), "Debe fallar por nombre vacío");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Debe fallar por email vacío");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")), "Debe fallar por contraseña vacía");
    }

    @Test
    void emailInvalido_debeFallarValidacion() {
        User user = new User();
        user.setNombre("Ana");
        user.setEmail("correo-invalido");
        user.setPassword("12345678");
        user.setRol(new Role());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    void nombreSoloEspacios_debeFallarValidacion() {
    User user = new User();
    user.setNombre("    "); // solo espacios
    user.setEmail("juan@email.com");
    user.setPassword("12345678");
    user.setRol(new Role());

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("nombre")), "Debe fallar por nombre con solo espacios");
    }

    @Test
    void emailMalFormadoPeroNoVacio_debeFallarValidacion() {
    User user = new User();
    user.setNombre("Juan");
    user.setEmail("correo_invalido"); // no es un email válido
    user.setPassword("12345678");
    user.setRol(new Role());

    Set<ConstraintViolation<User>> violations = validator.validate(user);
    assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")), "Debe fallar por email mal formado");
    }

    @Test
    void passwordNulo_debeFallarValidacion() {
        User user = new User();
        user.setNombre("Ana");
        user.setEmail("ana@example.com");
        user.setPassword(null); // null
        user.setRol(new Role());

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")), "Debe fallar por contraseña nula");
    }

    @Test
void constructorCompleto_debeAsignarTodosLosCampos() {
    Role role = new Role(3, "REPARTIDOR");
    User user = new User(7L, "Luis", "luis@mail.com", "secreta", role);

    assertEquals(7L, user.getId());
    assertEquals("Luis", user.getNombre());
    assertEquals("luis@mail.com", user.getEmail());
    assertEquals("secreta", user.getPassword());
    assertEquals(role, user.getRol());
}

    @Test
    void gettersYSetters_debenAsignarYObtenerCorrectamente() {
    Role role = new Role(4, "CLIENTE");
    User user = new User();
    user.setId(9L);
    user.setUsuarioId(100L);
    user.setNombre("Pedro");
    user.setEmail("pedro@mail.com");
    user.setPassword("clave456");
    user.setRol(role);

    assertEquals(9L, user.getId());
    assertEquals(100L, user.getUsuarioId());
    assertEquals("Pedro", user.getNombre());
    assertEquals("pedro@mail.com", user.getEmail());
    assertEquals("clave456", user.getPassword());
    assertEquals(role, user.getRol());
    }

}
