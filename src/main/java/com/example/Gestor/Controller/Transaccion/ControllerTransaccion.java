package com.example.Gestor.Controller.Transaccion;

import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.DTO.Transaccion.NewTransaccionDTO_CategoriaDTO;
import com.example.Gestor.DTO.Transaccion.TransaccionDTO;
import com.example.Gestor.Exception.TotalResponse;
import com.example.Gestor.Mappers.MapperTransacion.MapperTransaccion;
import com.example.Gestor.Model.Entity.Presupuesto;
import com.example.Gestor.Model.Entity.Tipo;
import com.example.Gestor.Model.Entity.Movimientos;
import com.example.Gestor.Model.Service.Transaccion.ITransaccionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
@Tag(name = "Transaccion", description = "Gestión de Transaccion")
public class ControllerTransaccion {

    @Autowired
    private ITransaccionService iTransferenciaService;

    @Autowired
    private MapperTransaccion mapperTransaccion;

    private final Logger logger = LoggerFactory.getLogger(ControllerTransaccion.class);

    /**
     * Obtiene todas las transacciones de un usuario.
     *
     * @param id_User ID del usuario.
     * @return Lista de transacciones en formato DTO.
     */
    @GetMapping("/transacciones/{id_User}")
    @Operation(summary = "Obtener transacciones por usuario", description = "Obtiene todas las transacciones de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontradas"),
            @ApiResponse(responseCode = "204", description = "No se encontraron transacciones")
    })
    public ResponseEntity<?> obtenerTransacciones(@PathVariable long id_User) {
        logger.info("Obteniendo transacciones para el usuario ID: {}", id_User);

        List<Movimientos> transacciones = iTransferenciaService.findAllByUsuario(id_User);
        if (transacciones.isEmpty()) {
            logger.warn("No se encontraron transacciones para el usuario ID: {}", id_User);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<TransaccionDTO> transaccionDTOS = mapperTransaccion.toDTOL(transacciones);
        logger.info("Movimientos obtenidas exitosamente para el usuario ID: {}", id_User);

        return ResponseEntity.ok(transaccionDTOS);
    }

    @GetMapping("/transaccion/{id}")
    @Operation(summary = "Obtener movimiento", description = "Obtiene un movimiento por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Movimineto recuperado correctamente"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado")
    })
    public ResponseEntity<?> obtenerMovimiento(@PathVariable long id) {
        logger.info("Consultando movimiento con ID: {}", id);
        Movimientos movimientos = iTransferenciaService.findById(id);

        if (movimientos == null) {
            logger.warn("No se encontró el movimiento con ID: {}", id);
            return  ResponseEntity.ok("Movimiento no encontrado");
        }

        TransaccionDTO transaccionDTO = mapperTransaccion.toDTO(movimientos);
        logger.info("Movimiento recuperado: {}", transaccionDTO);

        return ResponseEntity.ok(transaccionDTO);
    }

    /**
     * Obtiene las transacciones de un usuario filtradas por categoría.
     *
     * @param idUsuario    ID del usuario.
     * @param tipoId ID de la categoría.
     * @return Lista de transacciones filtradas.
     */
    @GetMapping("/transaccionesByCategoria/{idUsuario}/{tipoId}")
    @Operation(summary = "Obtener transacciones por categoría", description = "Obtiene las transacciones de un usuario filtradas por una categoría específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos encontradas"),
            @ApiResponse(responseCode = "204", description = "No se encontraron transacciones")
    })
    public ResponseEntity<?> obtenerTransaccionesByCategoria(
            @PathVariable("idUsuario") long idUsuario,
            @PathVariable("tipoId") int tipoId) { // Recibir como int

        logger.info("🔍 Buscando transacciones para usuario ID: {} en categoría ID: {}", idUsuario, tipoId);

        Tipo tipo;
        try {
            tipo = Tipo.fromInt(tipoId); // Convertir int a Tipo
        } catch (IllegalArgumentException e) {
            logger.error("❌ Tipo inválido: {}", tipoId);
            return ResponseEntity.badRequest().build();
        }

        List<Movimientos> transacciones = iTransferenciaService.filterByCategoria(idUsuario, tipo);
        if (transacciones.isEmpty()) {
            logger.warn("⚠️ No se encontraron transacciones.");
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }

        List<TransaccionDTO> transaccionDTOS = mapperTransaccion.toDTOL(transacciones);
        return ResponseEntity.ok(transaccionDTOS);
    }


    /**
     * Obtiene el total de ingresos de un usuario.
     *
     * @param id ID del usuario.
     * @return Total de ingresos como BigDecimal.
     */
    @GetMapping("/total-ingresos/{id}")
    @Operation(summary = "Obtener total de ingresos", description = "Obtiene el total de ingresos de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de ingresos calculado"),
            @ApiResponse(responseCode = "404", description = "No se pudo calcular el total de ingresos")
    })
    public ResponseEntity<TotalResponse> obtenerTotalIngresos(@PathVariable long id) {
        logger.info("Calculando total de ingresos para usuario ID: {}", id);

        BigDecimal totalIngresos = iTransferenciaService.obtenerTotalIngresos(id);
        if (totalIngresos == null) {
            logger.error("No se pudo calcular el total de ingresos para usuario ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new TotalResponse(BigDecimal.ZERO));
        }

        return ResponseEntity.ok(new TotalResponse(totalIngresos));
    }


    /**
     * Obtiene el total de gastos de un usuario.
     *
     * @param id ID del usuario.
     * @return Total de gastos como BigDecimal.
     */
    @GetMapping("/total-gastos/{id}")
    @Operation(summary = "Obtener total de gastos", description = "Obtiene el total de gastos de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Total de gastos calculado"),
            @ApiResponse(responseCode = "404", description = "No se pudo calcular el total de gastos")
    })
    public ResponseEntity<BigDecimal> obtenerTotalGastos(@PathVariable long id) {
        logger.info("Calculando total de gastos para usuario ID: {}", id);

        BigDecimal totalGastos = iTransferenciaService.obtenerTotalGastos(id);
        if (totalGastos == null) {
            logger.error("No se pudo calcular el total de gastos para usuario ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(BigDecimal.ZERO);
        }

        return ResponseEntity.ok(totalGastos);
    }

    /**
     * Crea una nueva transacción.
     *
     * @param newTransaccionDTOCategoriaDTO DTO con los datos de la nueva transacción.
     * @return Mensaje de éxito o error.
     */
    @PostMapping("/Create_Transaccion")
    @Operation(summary = "Crear transacción", description = "Crea una nueva transacción para un usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción creada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error al crear la transacción")
    })
    public ResponseEntity<String> addCreate_Transaccion(@RequestBody NewTransaccionDTO_CategoriaDTO newTransaccionDTOCategoriaDTO) {
        logger.info("Creando transacción para usuario ID: {}", newTransaccionDTOCategoriaDTO.getId_User());

        boolean realizado = iTransferenciaService.Create(
                newTransaccionDTOCategoriaDTO.getId_User(),
                newTransaccionDTOCategoriaDTO.getCantidad(),
                newTransaccionDTOCategoriaDTO.getDescripcion(),
                newTransaccionDTOCategoriaDTO.getTipo());

        if (realizado) {
            return ResponseEntity.ok("Transacción agregada correctamente");
        } else {
            logger.error("Error al agregar transacción para usuario ID: {}", newTransaccionDTOCategoriaDTO.getId_User());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al agregar la transacción");
        }
    }

    /**
     * Modifica una transacción existente.
     *
     * @param id ID de la transacción a modificar.
     * @param transaccionDTO Datos actualizados de la transacción.
     * @return Mensaje de éxito o error.
     */
    @PutMapping("/TransaccionModificar/{id}")
    @Operation(summary = "Modificar transacción", description = "Modifica una transacción existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción modificada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "500", description = "Error al modificar la transacción")
    })
    public ResponseEntity<?> modificarTransaccion(@PathVariable long id, @RequestBody TransaccionDTO transaccionDTO) {
        logger.info("Modificando transacción ID: {}", id);

        if (transaccionDTO == null) {
            logger.error("Solicitud inválida: El DTO de modificación es nulo");
            return ResponseEntity.badRequest().body("Error: Datos inválidos");
        }

        Map<String, Object> response = new HashMap<>();
        try {
            Movimientos transaccionModificada = iTransferenciaService.Update(id, transaccionDTO);
            response.put("mensaje", "Transacción actualizada con éxito");
            response.put("transaccion", transaccionModificada.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al modificar transacción ID: {}. Detalles: {}", id, e.getMessage());
            response.put("mensaje", "Error al modificar la transacción");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Elimina una transacción por su ID.
     *
     * @param id ID de la transacción a eliminar.
     * @return Mensaje de éxito o error.

    @DeleteMapping("/transaccionBorrar/{id}")
    @Operation(summary = "Eliminar transacción", description = "Elimina una transacción por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción eliminada exitosamente"),
            @ApiResponse(responseCode = "500", description = "Error al eliminar la transacción")
    })
    public ResponseEntity<?> deleteTransaccion(@PathVariable long id) {
        logger.info("Eliminando transacción ID: {}", id);

        Map<String, Object> response = new HashMap<>();
        try {
            iTransferenciaService.delete(id);
            response.put("mensaje", "Transacción eliminada con éxito");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error al eliminar transacción ID: {}. Detalles: {}", id, e.getMessage());
            response.put("mensaje", "Error al eliminar la transacción");
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
     */
}
