package com.example.Gestor.Tests.PresupuestoTest;

import com.example.Gestor.Controller.Presupuesto.PresupuestoController;
import com.example.Gestor.DTO.Presupuesto.NewPresupuestoDTO;
import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.Model.Entity.Presupuesto;
import com.example.Gestor.Model.Service.Presupuesto.IPresupuestoService;
import com.example.Gestor.Mappers.MapperPresupuesto.MapperPresupuesto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PresupuestoControllerTest {

    @Mock
    private IPresupuestoService presupuestoService;

    @Mock
    private MapperPresupuesto mapperPresupuesto;

    @InjectMocks
    private PresupuestoController presupuestoController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(presupuestoController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }


    // Test para crear presupuesto
    @Test
    public void testCrearPresupuesto() throws Exception {
        NewPresupuestoDTO newPresupuestoDTO = new NewPresupuestoDTO(500.0, "Presupuesto mensual","abril", 1L);
        when(presupuestoService.Create(anyDouble(), anyString(),anyString(), anyLong())).thenReturn(true);

        mockMvc.perform(post("/api/crearPresupuesto")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newPresupuestoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Presupuesto agregado correctamente"));

        verify(presupuestoService, times(1)).Create(anyDouble(), anyString(),anyString(), anyLong());
    }

    // Test para eliminar presupuesto
    @Test
    public void testDeletePresupuesto() throws Exception {
        long presupuestoId = 1L;
        doNothing().when(presupuestoService).delete(presupuestoId);

        mockMvc.perform(delete("/api/presupuestoBorrar/{id}", presupuestoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Presupuesto eliminado con éxito"));

        verify(presupuestoService, times(1)).delete(presupuestoId);
    }

    // Test para obtener presupuesto por ID
    @Test
    public void testObtenerPresupuesto() throws Exception {
        long presupuestoId = 1L;
        Presupuesto presupuesto = new Presupuesto(presupuestoId, null, 500.0, "Presupuesto mensual","abril", LocalDateTime.now());
        PresupuestoDTO presupuestoDTO = new PresupuestoDTO(1L,500.0, "Presupuesto mensual", LocalDateTime.now());
        when(presupuestoService.findById(presupuestoId)).thenReturn(presupuesto);
        when(mapperPresupuesto.toDTO(presupuesto)).thenReturn(presupuestoDTO);

        mockMvc.perform(get("/api/presupuesto/{id}", presupuestoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadLimite").value(500.0))
                .andExpect(jsonPath("$.descripcion").value("Presupuesto mensual"));

        verify(presupuestoService, times(1)).findById(presupuestoId);
        verify(mapperPresupuesto, times(1)).toDTO(presupuesto);
    }

    // Test para obtener presupuestos por usuario
    @Test
    public void testObtenerPresupuestosPorUsuario() throws Exception {
        long usuarioId = 1L;
        Presupuesto presupuesto = new Presupuesto(1L, null, 500.0, "Presupuesto mensual","abreil",LocalDateTime.now());
        PresupuestoDTO presupuestoDTO = new PresupuestoDTO(1L,500.0, "Presupuesto mensual", LocalDateTime.now());
        when(presupuestoService.findAllByUser(usuarioId)).thenReturn(Collections.singletonList(presupuesto));
        when(mapperPresupuesto.toDTOL(anyList())).thenReturn(Collections.singletonList(presupuestoDTO));

        mockMvc.perform(get("/api/usuarioPresupuestos/{idUser}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidadLimite").value(500.0))
                .andExpect(jsonPath("$[0].descripcion").value("Presupuesto mensual"));

        verify(presupuestoService, times(1)).findAllByUser(usuarioId);
        verify(mapperPresupuesto, times(1)).toDTOL(anyList());
    }

    // Test para modificar presupuesto
    @Test
    public void testModificarPresupuesto_Success() throws Exception {
        long presupuestoId = 1L;
        PresupuestoDTO presupuestoDTO = new PresupuestoDTO(1L,600.0, "Presupuesto modificado", LocalDateTime.now());

        // No se realiza stubbing ya que Update es void y no se requiere simular ningún comportamiento.

        mockMvc.perform(put("/api/Presupuesto_Modi/{id}", presupuestoId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(presupuestoDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Presupuesto modificado")); // <-- corregido

        // Verificamos que se llamó al método Update con el id correcto y cualquier instancia de PresupuestoDTO
        verify(presupuestoService, times(1)).Update(eq(presupuestoId), any(PresupuestoDTO.class));
    }


    @Test
    public void testObtenerPorcentajeGasto() throws Exception {
        long usuarioId = 1L;
        double porcentajeGastado = 75.5;

        when(presupuestoService.calcularPorcentajeGasto(usuarioId)).thenReturn(porcentajeGastado);

        // Obtener el mes actual en español, en minúsculas (como viene por defecto)
        String mesActual = LocalDate.now()
                .getMonth()
                .getDisplayName(TextStyle.FULL, new Locale("es"));

        String mensajeEsperado = String.format("El usuario ha gastado el 75,50%% de su presupuesto del mes de %s.", mesActual);

        mockMvc.perform(get("/api/porcentaje-gastado/{usuarioId}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value(mensajeEsperado));

        verify(presupuestoService, times(1)).calcularPorcentajeGasto(usuarioId);
    }

    // Test para obtener presupuesto actual de un usuario
    @Test
    public void testPresupuestoActual() throws Exception {
        long perfilId = 1L;
        Presupuesto presupuesto = new Presupuesto(1L, null, 500.0, "Presupuesto mensual","abril", LocalDateTime.now());
        PresupuestoDTO presupuestoDTO = new PresupuestoDTO(1L,500.0, "Presupuesto mensual", LocalDateTime.now());
        when(presupuestoService.presupuestoactualuser(perfilId)).thenReturn(Collections.singletonList(presupuesto));
        when(mapperPresupuesto.toDTO(presupuesto)).thenReturn(presupuestoDTO);

        mockMvc.perform(get("/api/presupuestoactual/{perfilId}", perfilId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cantidadLimite").value(500.0))
                .andExpect(jsonPath("$.descripcion").value("Presupuesto mensual"));

        verify(presupuestoService, times(1)).presupuestoactualuser(perfilId);
        verify(mapperPresupuesto, times(1)).toDTO(presupuesto);
    }

    @Test
    public void testBuscarPresupuestosPorPerfilYFecha() throws Exception {
        Long perfilId = 1L;
        LocalDate fecha = LocalDate.of(2025, 4, 6);  // Ejemplo de fecha
        LocalDateTime fechaInicio = fecha.atStartOfDay();

        // Simular un presupuesto encontrado
        Presupuesto presupuesto = new Presupuesto(1L, null, 500.0, "Presupuesto mensual","abril",fechaInicio);
        PresupuestoDTO presupuestoDTO = new PresupuestoDTO(1L,500.0, "Presupuesto mensual", fechaInicio);

        // Simular la llamada al servicio
        when(presupuestoService.findPresupuestoByPerfilAndFecha(perfilId, fechaInicio)).thenReturn(List.of(presupuesto));
        when(mapperPresupuesto.toDTOL(anyList())).thenReturn(List.of(presupuestoDTO));

        // Llamada al endpoint
        mockMvc.perform(get("/api/buscar/{perfilId}/{fecha}", perfilId, fecha))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].cantidadLimite").value(500.0))
                .andExpect(jsonPath("$[0].descripcion").value("Presupuesto mensual"));

        verify(presupuestoService, times(1)).findPresupuestoByPerfilAndFecha(perfilId, fechaInicio);
        verify(mapperPresupuesto, times(1)).toDTOL(anyList());
    }

    // Test para el caso en que no se encuentran presupuestos para un perfil y fecha
    @Test
    public void testBuscarPresupuestosPorPerfilYFechaNoEncontrados() throws Exception {
        Long perfilId = 1L;
        LocalDate fecha = LocalDate.of(2025, 4, 6);  // Ejemplo de fecha
        LocalDateTime fechaInicio = fecha.atStartOfDay();

        // Simular que no se encontraron presupuestos
        when(presupuestoService.findPresupuestoByPerfilAndFecha(perfilId, fechaInicio)).thenReturn(Collections.emptyList());

        // Llamada al endpoint
        mockMvc.perform(get("/api/buscar/{perfilId}/{fecha}", perfilId, fecha))
                .andExpect(status().isNoContent())
                .andExpect(content().string("No se encontraron presupuestos"));

        verify(presupuestoService, times(1)).findPresupuestoByPerfilAndFecha(perfilId, fechaInicio);
    }
}
