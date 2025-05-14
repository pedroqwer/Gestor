package com.example.Gestor.Tests.TransaccionTest;

import com.example.Gestor.Controller.Transaccion.ControllerTransaccion;
import com.example.Gestor.DTO.Transaccion.NewTransaccionDTO_CategoriaDTO;
import com.example.Gestor.DTO.Transaccion.TransaccionDTO;
import com.example.Gestor.Mappers.MapperTransacion.MapperTransaccion;
import com.example.Gestor.Model.Entity.Movimientos;
import com.example.Gestor.Model.Entity.Tipo;
import com.example.Gestor.Model.Service.Transaccion.ITransaccionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ControllerTransaccionTest {

    @Mock
    private ITransaccionService iTransferenciaService;

    @Mock
    private MapperTransaccion mapperTransaccion;

    @InjectMocks
    private ControllerTransaccion controllerTransaccion;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private Movimientos transaccion;
    private TransaccionDTO transaccionDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controllerTransaccion).build();

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        transaccion = new Movimientos();

        transaccionDTO = new TransaccionDTO();
        transaccionDTO.setCantidad(100.00);
        transaccionDTO.setDescripcion("Compra de prueba");
    }

    @Test
    void obtenerMovimiento_CuandoExiste_DeberiaRetornarDTO() {
        // Arrange
        long movimientoId = 1L;
        Movimientos movimiento = new Movimientos();
        movimiento.setId(movimientoId);
        movimiento.setCantidad(100.0);
        movimiento.setDescripcion("Transferencia");
        movimiento.setFecha(LocalDateTime.now());

        TransaccionDTO dto = new TransaccionDTO(movimientoId, 100.0, "Transferencia", movimiento.getFecha(),Tipo.GASTO);

        when(iTransferenciaService.findById(movimientoId)).thenReturn(movimiento);
        when(mapperTransaccion.toDTO(movimiento)).thenReturn(dto);

        // Act
        ResponseEntity<?> response = controllerTransaccion.obtenerMovimiento(movimientoId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof TransaccionDTO);
        assertEquals(dto, response.getBody());
    }

    @Test
    void obtenerMovimiento_CuandoNoExiste_DeberiaRetornarMensaje() {
        // Arrange
        long movimientoId = 99L;
        when(iTransferenciaService.findById(movimientoId)).thenReturn(null);

        // Act
        ResponseEntity<?> response = controllerTransaccion.obtenerMovimiento(movimientoId);

        // Assert
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Movimiento no encontrado", response.getBody());
    }

    /** TEST OBTENER TODAS LAS TRANSACCIONES */
    @Test
    void testObtenerTransacciones() throws Exception {
        // Crear entidad de prueba
        Movimientos transaccion = new Movimientos();
        transaccion.setId(1L);
        transaccion.setCantidad(100.00);  // Asegurar que se setea correctamente
        transaccion.setDescripcion("Compra de prueba");

        List<Movimientos> transacciones = Collections.singletonList(transaccion);
        List<TransaccionDTO> transaccionDTOList = Collections.singletonList(new TransaccionDTO(1L,100.00, "Compra de prueba",LocalDateTime.now(),Tipo.GASTO));

        when(iTransferenciaService.findAllByUsuario(anyLong())).thenReturn(transacciones);
        when(mapperTransaccion.toDTOL(transacciones)).thenReturn(transaccionDTOList);

        mockMvc.perform(get("/api/transacciones/{id_User}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].cantidad").value(100.00))
                .andExpect(jsonPath("$[0].descripcion").value("Compra de prueba"));
    }

    @Test
    void testObtenerTransaccionesByCategoria() throws Exception {
        // Datos de prueba
        long idUsuario = 1L;
        long tipoId = 0L; // Tipo como número (0 o 1)
        List<Movimientos> transacciones = List.of(transaccion);
        List<TransaccionDTO> transaccionDTOList = List.of(transaccionDTO);

        // Configuración de los mocks
        when(iTransferenciaService.filterByCategoria(idUsuario, Tipo.values()[(int) tipoId])).thenReturn(transacciones);
        when(mapperTransaccion.toDTOL(transacciones)).thenReturn(transaccionDTOList);

        // Prueba del endpoint con tipoId como número
        mockMvc.perform(get("/api/transaccionesByCategoria/{idUsuario}/{tipoId}", idUsuario, tipoId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(transaccionDTOList.size())) // Verifica la cantidad de elementos
                .andExpect(jsonPath("$[0].descripcion").value(transaccionDTO.getDescripcion())); // Verifica un campo específico
                //.andExpect(jsonPath("$[0].cantidad").value(transaccionDTO.getCantidad())); // Verifica otro campo relevante
    }

    /** TEST OBTENER TOTAL INGRESOS */
    @Test
    void testObtenerTotalIngresos() throws Exception {
        when(iTransferenciaService.obtenerTotalIngresos(anyLong())).thenReturn(new BigDecimal("500.00"));

        mockMvc.perform(get("/api/total-ingresos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(500.00));
    }

    /** TEST OBTENER TOTAL GASTOS */
    @Test
    void testObtenerTotalGastos() throws Exception {
        when(iTransferenciaService.obtenerTotalGastos(anyLong())).thenReturn(new BigDecimal("200.00"));

        mockMvc.perform(get("/api/total-gastos/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("200.00"));
    }

    /** TEST CREAR TRANSACCIÓN */
    @Test
    void testCrearTransaccion() throws Exception {
        NewTransaccionDTO_CategoriaDTO newTransaccion = new NewTransaccionDTO_CategoriaDTO();
        newTransaccion.setId_User(1L);
        newTransaccion.setCantidad(150.00);
        newTransaccion.setDescripcion("Pago de servicio");
        //newTransaccion.setDescripcionCategoria("Servicios");
        newTransaccion.setTipo(Tipo.GASTO); // Suponiendo que Tipo es un ENUM

        // Corregido: Especificamos el tipo exacto en los matchers de Mockito
        when(iTransferenciaService.Create(
                anyLong(), any(Double.class), anyString(), any(Tipo.class)))
                .thenReturn(true);

        mockMvc.perform(post("/api/Create_Transaccion") // Cambiado de PUT a POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newTransaccion)))
                .andExpect(status().isOk())
                .andExpect(content().string("Transacción agregada correctamente"));
    }
    @Test
    void testModificarTransaccion_Success() throws Exception {
        long id = 1L;
        TransaccionDTO transaccionDTO = new TransaccionDTO(id, 120.00, "Modificado", LocalDateTime.now(), Tipo.INGRESO);
        Movimientos transaccionModificada = new Movimientos();
        transaccionModificada.setId(id);
        transaccionModificada.setCantidad(120.00);
        transaccionModificada.setDescripcion("Modificado");

        when(iTransferenciaService.Update(eq(id), any(TransaccionDTO.class))).thenReturn(transaccionModificada);

        mockMvc.perform(put("/api/TransaccionModificar/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaccionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Transacción actualizada con éxito"))
                .andExpect(jsonPath("$.transaccion").isNotEmpty());
    }

    @Test
    void testModificarTransaccion_Error() throws Exception {
        long id = 1L;
        TransaccionDTO transaccionDTO = new TransaccionDTO();

        when(iTransferenciaService.Update(eq(id), any(TransaccionDTO.class)))
                .thenThrow(new RuntimeException("Error interno"));

        mockMvc.perform(put("/api/TransaccionModificar/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaccionDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.mensaje").value("Error al modificar la transacción"))
                .andExpect(jsonPath("$.error").value("Error interno"));
    }
}
