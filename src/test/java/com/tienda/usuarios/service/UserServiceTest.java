package com.tienda.usuarios.service;

import com.tienda.usuarios.dto.UserRequestDTO;
import com.tienda.usuarios.dto.UserResponseDTO;
import com.tienda.usuarios.model.Role;
import com.tienda.usuarios.model.User;
import com.tienda.usuarios.repository.RoleRepository;
import com.tienda.usuarios.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private Role role;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1);
        role.setNombre("CLIENTE");

        user = new User();
        user.setId(100L);
        user.setUsuarioId(1L);
        user.setNombre("Juan");
        user.setEmail("juan@example.com");
        user.setPassword("encodedPass");
        user.setRol(role);
    }

    @Test
    void getAllUsers_deberiaRetornarListaCompleta() {
        List<User> users = List.of(user);
        when(userRepository.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(1, result.size());
        assertEquals("Juan", result.get(0).getNombre());
    }

    @Test
    void getUserById_usuarioExiste_deberiaRetornarDTO() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserById(1L);

        assertEquals(user.getNombre(), result.nombre());
        assertEquals(user.getEmail(), result.email());
        assertEquals(user.getRol().getNombre(), result.rol());
    }

    @Test
    void getUserById_usuarioNoExiste_deberiaLanzarExcepcion() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.getUserById(1L));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void getUserByEmail_usuarioExiste_deberiaRetornarDTO() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));

        UserResponseDTO result = userService.getUserByEmail("juan@example.com");

        assertEquals(user.getNombre(), result.nombre());
        assertEquals(user.getEmail(), result.email());
    }

    @Test
    void getUserByEmail_usuarioNoExiste_deberiaLanzarExcepcion() {
        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.getUserByEmail("juan@example.com"));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    @Test
    void registrarUsuario_datosValidos_deberiaCrearUsuario() {
        UserRequestDTO request = new UserRequestDTO("Juan", "juan@example.com", "1234", role);

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("CLIENTE")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("1234")).thenReturn("encoded1234");
        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(999L);
            return u;
        });

        UserResponseDTO response = userService.registrarUsuario(request);

        assertEquals("Juan", response.nombre());
        assertEquals("juan@example.com", response.email());
        assertEquals("CLIENTE", response.rol());
        assertEquals(999L, response.usuarioId());
    }


    @Test
    void registrarUsuario_emailExistente_deberiaLanzarExcepcion() {
        UserRequestDTO request = new UserRequestDTO("Juan", "juan@example.com", "1234", role);

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.of(user));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registrarUsuario(request));

        assertEquals("El correo ya está registrado", ex.getMessage());
    }


    @Test
    void registrarUsuario_rolNoExiste_deberiaLanzarExcepcion() {
        Role invalidRole = new Role();
        invalidRole.setNombre("REPARTIDOR");

        UserRequestDTO request = new UserRequestDTO("Juan", "juan@example.com", "1234", invalidRole);

        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
        when(roleRepository.findByNombre("REPARTIDOR")).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.registrarUsuario(request));

        assertEquals("El rol no existe en la base de datos: REPARTIDOR", ex.getMessage());
    }


    @Test
    void deleteUser_usuarioExiste_deberiaEliminar() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> userService.deleteUser(1L));

        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUser_usuarioNoExiste_deberiaLanzarExcepcion() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.deleteUser(1L));

        assertEquals("Usuario no encontrado", ex.getMessage());
    }
}
