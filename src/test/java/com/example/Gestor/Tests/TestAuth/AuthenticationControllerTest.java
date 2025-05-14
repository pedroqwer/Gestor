package com.example.Gestor.Tests.TestAuth;

import com.example.Gestor.Controller.Autentication.AuthenticationController;
import com.example.Gestor.DTO.Usuario.CrearUsuarioDTO;
import com.example.Gestor.DTO.Usuario.LoginUsuarioDTO;
import com.example.Gestor.Mappers.MapperClas;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Entity.Rol;
import com.example.Gestor.Model.Entity.RoleType;
import com.example.Gestor.Model.Entity.Usuario;
import com.example.Gestor.Model.Repository.IUsuarioRepository.IRolRepository;
import com.example.Gestor.Model.Service.Authentication.IAuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private IAuthenticationService authenticationService;

    @Mock
    private MapperClas mapper;

    @Mock
    private IRolRepository roleRepository;

    @InjectMocks
    private AuthenticationController authenticationController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController).build();
    }

    /** 🔹 TEST 1: Registro de perfil exitoso */
    @Test
    void testRegisterPerfil() throws Exception {
        // Datos simulados
        CrearUsuarioDTO crearUsuarioDTO = new CrearUsuarioDTO();
        crearUsuarioDTO.setNombre("Juan");
        crearUsuarioDTO.setApellido("Pérez");
        crearUsuarioDTO.setUsername("juanperez");
        crearUsuarioDTO.setPassword("password123");
        crearUsuarioDTO.setEmail("juan@example.com");
        crearUsuarioDTO.setDni("12345678");
        crearUsuarioDTO.setTelefono("987654321");
        crearUsuarioDTO.setRoles(Collections.singletonList(1));

        Perfil perfilMock = new Perfil();
        perfilMock.setUsername(crearUsuarioDTO.getUsername());

        Rol rolMock = new Rol();
        rolMock.setId(1);
        rolMock.setNombre(RoleType.USER);

        // Simulación de mapeo y autenticación
        when(mapper.mapType(any(), any())).thenReturn(perfilMock);
        when(roleRepository.findByNombreIn(any())).thenReturn(List.of(rolMock));
        when(authenticationService.signup(any())).thenReturn(new Usuario());

        // Prueba de la API
        mockMvc.perform(post("/auth/signup/perfil")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(crearUsuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Perfil registrado correctamente"));
    }

    /** 🔹 TEST 2: Autenticación exitosa */
    @Test
    void testAuthenticateSuccess() throws Exception {
        LoginUsuarioDTO loginUsuarioDTO = new LoginUsuarioDTO("usuario_valido", "clave_correcta");

        when(authenticationService.authenticate(any())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUsuarioDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }

    /** 🔹 TEST 3: Autenticación con credenciales incorrectas */
    @Test
    void testAuthenticateFailure() throws Exception {
        LoginUsuarioDTO loginUsuarioDTO = new LoginUsuarioDTO("usuario_invalido", "clave_incorrecta");

        when(authenticationService.authenticate(any())).thenThrow(new RuntimeException("Credenciales inválidas"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUsuarioDTO)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("Credenciales inválidas"));
    }
}