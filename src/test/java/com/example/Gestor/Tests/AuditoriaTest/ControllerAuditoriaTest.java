package com.example.Gestor.Tests.AuditoriaTest;

import com.example.Gestor.Controller.Auditoria.ControllerAuditoria;
import com.example.Gestor.DTO.Auditroria.AuditoriaDTO;
import com.example.Gestor.Mappers.MapperAuditoria.MapperAuditoria;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Service.Auditoria.IAuditoriaService;
import com.example.Gestor.Exception.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ControllerAuditoriaTest {

    @Mock
    private IAuditoriaService auditoriaService;

    @Mock
    private MapperAuditoria mapperAuditoria;

    @InjectMocks
    private ControllerAuditoria controllerAuditoria;

    private Auditoria auditoria;
    private Perfil perfil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        perfil = new Perfil();
        perfil.setNombre("Juan Pérez");

        auditoria = new Auditoria();
        auditoria.setId(1L);
        auditoria.setAccion("LOGIN");
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setDetalles("Inicio de sesión exitoso");
        auditoria.setPerfil(perfil);
    }

    @Test
    void obtenerAuditoria_ExisteAuditoria() {
        when(auditoriaService.findById(1L)).thenReturn(auditoria);

        ResponseEntity<?> response = controllerAuditoria.obtenerAuditoria(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Juan Pérez"));
    }

    @Test
    void obtenerAuditoria_NoExisteAuditoria() {
        when(auditoriaService.findById(1L)).thenThrow(new RuntimeException("No encontrada"));

        ResponseEntity<?> response = controllerAuditoria.obtenerAuditoria(1L);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("Auditoria no encontrada", response.getBody()); // corregido: compara con String plano
    }

    @Test
    void getAllAuditroriasByUser_ConResultados() {
        List<Auditoria> auditorias = List.of(auditoria);
        AuditoriaDTO expectedDTO = new AuditoriaDTO(1L,"Juan Pérez", "LOGIN", auditoria.getFecha(), "Inicio de sesión exitoso");

        when(auditoriaService.findAllByUserAuditoria(1L)).thenReturn(auditorias);
        when(mapperAuditoria.toDTOL(auditorias)).thenReturn(List.of(expectedDTO));

        ResponseEntity<?> response = controllerAuditoria.getAllAuditroriasByUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        List<?> actualList = (List<?>) response.getBody();
        AuditoriaDTO actualDTO = (AuditoriaDTO) actualList.get(0);

        assertEquals(expectedDTO.getNombreUsuario(), actualDTO.getNombreUsuario());
        assertEquals(expectedDTO.getAccion(), actualDTO.getAccion());
        assertEquals(expectedDTO.getFecha(), actualDTO.getFecha());
        assertEquals(expectedDTO.getDetalles(), actualDTO.getDetalles());
    }



    @Test
    void getAllAuditroriasByUser_SinResultados() {
        when(auditoriaService.findAllByUserAuditoria(1L)).thenReturn(Collections.emptyList());

        ResponseEntity<?> response = controllerAuditoria.getAllAuditroriasByUser(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("No se encontraron auditorías", response.getBody()); // corregido con tilde
    }

}
