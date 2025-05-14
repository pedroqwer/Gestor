package com.example.Gestor.Tests.PerfilTest;

import com.example.Gestor.Controller.Perfil.ControllerPerfil;
import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.DTO.Usuario.LoginUsuarioDTO;
import com.example.Gestor.Mappers.MapperPerfil.MApperPerfil;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Service.Perfil.IPerfilService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerPerfilTest {

    @InjectMocks
    private ControllerPerfil controllerPerfil;

    @Mock
    private IPerfilService perfilService;

    @Mock
    private MApperPerfil mapperPerfil;

    private Perfil perfil;
    private PerfilDTO perfilDTO;

    @BeforeEach
    void setUp() {
        perfil = new Perfil();
        perfil.setId(1L);
        perfil.setNombre("Usuario Test");

        perfilDTO = new PerfilDTO();
        perfilDTO.setNombre("Usuario Test");

    }

    // ✅ Test modificar perfil de usuario
    @Test
    void modificarPerfil_UsuarioExistente_DebeModificarPerfil() {
        long userId = 1L;
        when(perfilService.Update_By_User(userId, perfilDTO)).thenReturn(perfil);

        ResponseEntity<?> response = controllerPerfil.modificarPerfil(userId, perfilDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Perfil modificado con éxito"));
        verify(perfilService, times(1)).Update_By_User(userId, perfilDTO);
    }



    // ✅ Test obtener lista de perfiles
    @Test
    void obtenerPerfiles_DebeRetornarListaDePerfiles() {
        List<Perfil> perfiles = Arrays.asList(perfil);
        List<PerfilDTO> perfilDTOs = Arrays.asList(perfilDTO);

        when(perfilService.GetAll()).thenReturn(perfiles);
        when(mapperPerfil.toDTOL(perfiles)).thenReturn(perfilDTOs);

        ResponseEntity<?> response = controllerPerfil.obtenerPerfiles();

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Perfiles obtenidos exitosamente"));
        verify(perfilService, times(1)).GetAll();
    }

    // ✅ Test obtener perfil por ID
    @Test
    void obtenerPerfil_UsuarioExistente_DebeRetornarPerfil() {
        long userId = 1L;
        when(perfilService.GetById(userId)).thenReturn(perfil);
        when(mapperPerfil.toDTO(perfil)).thenReturn(perfilDTO);

        ResponseEntity<?> response = controllerPerfil.obtenerPerfil(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Perfil obtenido con éxito"));
        verify(perfilService, times(1)).GetById(userId);
    }

    // ❌ Test obtener perfil que no existe
    @Test
    void obtenerPerfil_UsuarioNoExiste_DebeRetornarError() {
        long userId = 999L;
        when(perfilService.GetById(userId)).thenThrow(new RuntimeException("Perfil no encontrado"));

        ResponseEntity<?> response = controllerPerfil.obtenerPerfil(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Perfil no encontrado"));
        verify(perfilService, times(1)).GetById(userId);
    }

    // ✅ Test eliminar perfil
    @Test
    void eliminarPerfil_UsuarioExistente_DebeEliminarPerfil() {
        long userId = 1L;
        doNothing().when(perfilService).Delete(userId);

        ResponseEntity<?> response = controllerPerfil.deletePerfil(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Perfil eliminado con éxito"));
        verify(perfilService, times(1)).Delete(userId);
    }

    // ❌ Test eliminar perfil que no existe
    @Test
    void eliminarPerfil_UsuarioNoExiste_DebeRetornarError() {
        long userId = 999L;
        doThrow(new RuntimeException("Error al eliminar el perfil")).when(perfilService).Delete(userId);

        ResponseEntity<?> response = controllerPerfil.deletePerfil(userId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al eliminar el perfil"));
        verify(perfilService, times(1)).Delete(userId);
    }

    @Test
    void obtenerIdByUsernameAndPass_CredencialesValidas_DebeRetornarIdUsuario() {
        String username = "usuario";
        String password = "1234";
        long userId = 42L;

        // Simulación del servicio, se ajusta al nuevo método del controlador
        when(perfilService.obtenerIdByUsernameAndPass(username, password)).thenReturn(userId);

        // Aquí llamamos al método pasando username y password directamente en el test
        ResponseEntity<?> response = controllerPerfil.obtenerIdByUsernameAndPass(username, password);

        assertEquals(200, response.getStatusCodeValue());

        // Verificar que la respuesta contiene el ID de usuario, no el mensaje completo
        assertTrue(response.getBody().toString().contains(String.valueOf(userId)));

        // Verificar que el método del servicio fue invocado una vez con los parámetros correctos
        verify(perfilService, times(1)).obtenerIdByUsernameAndPass(username, password);
    }

    @Test
    void obtenerIdByUsernameAndPass_CredencialesInvalidas_DebeRetornarUnauthorized() {
        String username = "usuario";
        String password = "incorrecto";

        // Simulación del servicio, cuando las credenciales no sean válidas, debe retornar null
        when(perfilService.obtenerIdByUsernameAndPass(username, password)).thenReturn(null);

        // Llamamos al método pasando directamente username y password
        ResponseEntity<?> response = controllerPerfil.obtenerIdByUsernameAndPass(username, password);

        // Verificamos que la respuesta sea un código 401 de "Unauthorized"
        assertEquals(401, response.getStatusCodeValue());

        // Verificar que la respuesta contiene el mensaje "Credenciales inválidas"
        assertTrue(response.getBody().toString().contains("Credenciales inválidas"));

        // Verificar que el método del servicio fue invocado una vez con los parámetros correctos
        verify(perfilService, times(1)).obtenerIdByUsernameAndPass(username, password);
    }

    @Test
    void obtenerTelefonoPorId_ExisteTelefono_DebeRetornarTelefono() {
        long userId = 1L;
        String telefono = "123456789";

        when(perfilService.obtenertelefonobyid(userId)).thenReturn(telefono);

        ResponseEntity<?> response = controllerPerfil.obtenerTelefonoPorId(userId);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(telefono, response.getBody());
        verify(perfilService, times(1)).obtenertelefonobyid(userId);
    }

    @Test
    void obtenerTelefonoPorId_TelefonoNoExiste_DebeRetornarNotFound() {
        long userId = 1L;

        when(perfilService.obtenertelefonobyid(userId)).thenReturn("");

        ResponseEntity<?> response = controllerPerfil.obtenerTelefonoPorId(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Teléfono no encontrado"));
        verify(perfilService, times(1)).obtenertelefonobyid(userId);
    }

    @Test
    void obtenerTelefonoPorId_ExcepcionEnServicio_DebeRetornarError500() {
        long userId = 1L;

        when(perfilService.obtenertelefonobyid(userId)).thenThrow(new RuntimeException("Falla técnica"));

        ResponseEntity<?> response = controllerPerfil.obtenerTelefonoPorId(userId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al obtener el teléfono"));
        verify(perfilService, times(1)).obtenertelefonobyid(userId);
    }

}
