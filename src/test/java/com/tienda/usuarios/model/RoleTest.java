package com.tienda.usuarios.model;

import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    private Validator validator;

    @BeforeEach
    void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void rolValido_debePasarValidacion() {
        Role role = new Role();
        role.setNombre("ADMIN");

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertTrue(violations.isEmpty());
    }

    @Test
    void rolSinNombre_debeFallarValidacion() {
        Role role = new Role(); // nombre null

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertFalse(violations.isEmpty(), "Debe lanzar error porque el nombre es obligatorio");

    }

    @Test
    void rolNombreVacio_debeFallarValidacion() {
        Role role = new Role();
        role.setNombre("   "); // nombre en blanco

        Set<ConstraintViolation<Role>> violations = validator.validate(role);
        assertFalse(violations.isEmpty(), "Debe fallar por nombre vacío");
    }

    @Test
    void constructorConParametros_debeAsignarValoresCorrectamente() {
        Role role = new Role(10, "ADMIN");

        assertEquals(10, role.getId());
        assertEquals("ADMIN", role.getNombre());
    }

    @Test
    void gettersYSetters_debenFuncionarCorrectamente() {
        Role role = new Role();
        role.setId(2);
        role.setNombre("CLIENTE");

        assertEquals(2, role.getId());
        assertEquals("CLIENTE", role.getNombre());
    }

}

