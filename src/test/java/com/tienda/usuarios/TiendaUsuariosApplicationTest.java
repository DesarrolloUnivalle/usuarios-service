package com.tienda.usuarios;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class TiendaUsuariosApplicationTest {

    @Test
    void main_deberiaEjecutarSinErrores() {
        assertDoesNotThrow(() -> TiendaUsuariosApplication.main(new String[]{}));
    }
}
