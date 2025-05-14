package com.example.Gestor.Controller.Presupuesto;

import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.DTO.Presupuesto.NewPresupuestoDTO;
import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.DTO.Transaccion.TransaccionDTO;
import com.example.Gestor.Exception.Response;
import com.example.Gestor.Mappers.MapperPresupuesto.MapperPresupuesto;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Entity.Presupuesto;
import com.example.Gestor.Model.Service.Presupuesto.IPresupuestoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
@Tag(name = "Presupuesto", description = "Gestión de Presupuesto")
public class PresupuestoController {

    @Autowired
    private IPresupuestoService iPresupuestoService;

    @Autowired
    private MapperPresupuesto mapperPresupuesto;

    private final Logger logger = LoggerFactory.getLogger(PresupuestoController.class);

    /**
     * Crea un nuevo presupuesto con los datos proporcionados.
     *
     * @param newPresupuestoDTO DTO con los datos del nuevo presupuesto.
     * @return ResponseEntity con el resultado de la operación.
     */
    @PostMapping("/crearPresupuesto")
    @Operation(summary = "Crear presupuesto", description = "Crea un nuevo presupuesto con los datos proporcionados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presupuesto agregado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error al agregar el presupuesto")
    })
    public ResponseEntity<?> crearPresupuesto(@RequestBody NewPresupuestoDTO newPresupuestoDTO) {
        logger.info("Iniciando creación de presupuesto para el usuario: {}", newPresupuestoDTO.getId_user());

        boolean realizado = iPresupuestoService.Create(
                newPresupuestoDTO.getCantidadLimite(),
                newPresupuestoDTO.getDescripcion(),
                newPresupuestoDTO.getPresupuesto_del_mes(),
                newPresupuestoDTO.getId_user()
        );

        Map<String, Object> response = new HashMap<>();

        if (realizado) {
            response.put("mensaje", "Presupuesto agregado correctamente");
            logger.info("Presupuesto creado con éxito para el usuario: {}", newPresupuestoDTO.getId_user());
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "Error al agregar el presupuesto");
            logger.error("Error al crear el presupuesto para el usuario: {}", newPresupuestoDTO.getId_user());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Elimina un presupuesto por su ID.
     *
     * @param id Identificador del presupuesto a eliminar.
     * @return ResponseEntity con el resultado de la operación.
     */
    @DeleteMapping("/presupuestoBorrar/{id}")
    @Operation(summary = "Eliminar presupuesto", description = "Elimina un presupuesto por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presupuesto eliminado con éxito"),
            @ApiResponse(responseCode = "500", description = "Error al borrar el presupuesto")
    })
    public ResponseEntity<?> deletePresupuesto(@PathVariable long id) {
        logger.info("Iniciando eliminación de presupuesto con ID: {}", id);
        Map<String, Object> response = new HashMap<>();

        try {
            iPresupuestoService.delete(id);
            response.put("mensaje", "Presupuesto eliminado con éxito");
            logger.info("Presupuesto eliminado con éxito, ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("mensaje", "Error al borrar el presupuesto con la ID: " + id);
            response.put("error", e.getMessage());
            logger.error("Error al eliminar el presupuesto con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene un presupuesto por su ID.
     *
     * @param id Identificador del presupuesto a consultar.
     * @return ResponseEntity con el presupuesto encontrado o un error en caso contrario.
     */
    @GetMapping("/presupuesto/{id}")
    @Operation(summary = "Obtener presupuesto", description = "Obtiene un presupuesto por su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presupuesto recuperado correctamente"),
            @ApiResponse(responseCode = "404", description = "Presupuesto no encontrado")
    })
    public ResponseEntity<?> obtenerPresupuesto(@PathVariable long id) {
        logger.info("Consultando presupuesto con ID: {}", id);
        Presupuesto presupuesto = iPresupuestoService.findById(id);

        if (presupuesto == null) {
            logger.warn("No se encontró el presupuesto con ID: {}", id);
            return  ResponseEntity.ok("Presupuesto no encontrado");
        }

        PresupuestoDTO presupuestoDTO = mapperPresupuesto.toDTO(presupuesto);
        logger.info("Presupuesto recuperado: {}", presupuestoDTO);

        return ResponseEntity.ok(presupuestoDTO);
    }

    /**
     * Obtiene todos los presupuestos de un usuario por su ID.
     *
     * @param idUser ID del usuario.
     * @return ResponseEntity con la lista de presupuestos del usuario.
     */
    @GetMapping("/usuarioPresupuestos/{idUser}")
    @Operation(summary = "Obtener todos los presupuestos de un usuario", description = "Recupera la lista de todos los presupuestos asociados a un usuario dado su ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presupuestos encontrados"),
            @ApiResponse(responseCode = "204", description = "No se encontraron presupuestos")
    })
    public ResponseEntity<?> getAllPresupuestosByUser(@PathVariable long idUser) {
        logger.info("Obteniendo todos los presupuestos del usuario ID: {}", idUser);
        List<Presupuesto> presupuestos = iPresupuestoService.findAllByUser(idUser);

        if (presupuestos.isEmpty()) {
            logger.warn("No se encontraron presupuestos para el usuario ID: {}", idUser);
            return ResponseEntity.ok("No se encontraron presupuestos");
        }

        List<PresupuestoDTO> transaccionDTOS = mapperPresupuesto.toDTOL(presupuestos);
        logger.info("Presupuestos encontrados para el usuario ID: {}", idUser);

        return ResponseEntity.ok(transaccionDTOS);
    }

    @GetMapping("/buscar/{perfilId}/{fecha}")
    @Operation(summary = "Buscar presupuestos por perfil y fecha", description = "Busca presupuestos según el ID de perfil y una fecha específica.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Presupuestos encontrados"),
            @ApiResponse(responseCode = "204", description = "No se encontraron presupuestos")
    })
    public ResponseEntity<?> getPresupuestosByPerfilAndFecha(
            @PathVariable Long perfilId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        logger.info("Buscando presupuestos para el perfil ID: {} en la fecha: {}", perfilId, fecha);

        // Convertir LocalDate a LocalDateTime para buscar en la base de datos (ajustando a medianoche)
        LocalDateTime fechaInicio = fecha.atStartOfDay();

        List<Presupuesto> presupuestos = iPresupuestoService.findPresupuestoByPerfilAndFecha(perfilId, fechaInicio);

        if (presupuestos.isEmpty()) {
            logger.warn("No se encontraron presupuestos para el perfil ID: {} en la fecha: {}", perfilId, fecha);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body("No se encontraron presupuestos");
        }

        List<PresupuestoDTO> transaccionDTOS = mapperPresupuesto.toDTOL(presupuestos);
        logger.info("Presupuestos encontrados para el perfil ID: {}", perfilId);
        return ResponseEntity.ok(transaccionDTOS);
    }

    @PutMapping("/Presupuesto_Modi/{id}")
    @Operation(summary = "Modificar presupuesto de usuario", description = "Actualiza la información del presupuesto de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presupuesto modificado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<Map<String, String>> modificarPresupuesto(@PathVariable long id, @RequestBody PresupuestoDTO newprresupuesto) {
        logger.info("Modificando presupuesto del usuario con ID de presupuesto: {}", id);
        try {
            iPresupuestoService.Update(id, newprresupuesto);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Presupuesto modificado"));
        } catch (Exception e) {
            logger.error("Error al modificar presupuesto con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("mensaje", "Error al modificar el presupuesto"));
        }
    }

    @GetMapping("/porcentaje-gastado/{usuarioId}")
    @Operation(summary = "Obtener porcentaje gastado por usuario",
            description = "Este endpoint calcula y devuelve el porcentaje de presupuesto gastado por un usuario en el mes actual.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Porcentaje de gasto calculado y retornado"),
            @ApiResponse(responseCode = "400", description = "Error en el cálculo del porcentaje de gasto o presupuesto no encontrado")
    })
    public ResponseEntity<Map<String, Object>> obtenerPorcentajeGasto(@PathVariable long usuarioId) {
        try {
            double porcentajeGastado = iPresupuestoService.calcularPorcentajeGasto(usuarioId);

            String mesActual = LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, new Locale("es"));

            String mensaje = "El usuario ha gastado el " + String.format("%.2f", porcentajeGastado) +
                    "% de su presupuesto del mes de " + mesActual + ".";

            Map<String, Object> response = Map.of(
                    "mensaje", mensaje
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            // En caso de que no haya presupuesto o cualquier otro error lanzado desde el servicio
            Map<String, Object> errorResponse = Map.of(
                    "mensaje", e.getMessage()
            );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse); // Respondemos con el mensaje de error y un código de estado 400
        }
    }

   @GetMapping("/presupuestoactual/{perfilId}")
   @Operation(summary = "Obtener presupuesto actual de un usuario",
           description = "Este endpoint devuelve el presupuesto actual de un usuario, basado en su perfil, para el mes actual.")
   @ApiResponses(value = {
           @ApiResponse(responseCode = "200", description = "Presupuesto actual encontrado y retornado"),
           @ApiResponse(responseCode = "404", description = "Presupuesto no encontrado para este usuario en el mes actual"),
           @ApiResponse(responseCode = "500", description = "Error interno al intentar obtener el presupuesto")
   })
   public ResponseEntity<?> presupuestoActual(@PathVariable long perfilId) {
       try {
           // Obtener el presupuesto del mes actual
           List<Presupuesto> presupuesto = iPresupuestoService.presupuestoactualuser(perfilId);

           if (presupuesto == null || presupuesto.isEmpty()) {
               logger.warn("No se encontró un presupuesto actual para el usuario con ID: {}", perfilId);
               return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se encontraron presupuestos para este usuario en el mes actual.");
           }

           // Aquí asumimos que el presupuesto se devuelve en orden descendente por fecha, y tomamos el primero
           Presupuesto presupuestoActual = presupuesto.get(0);

           // Convertir el presupuesto a DTO
           PresupuestoDTO presupuestoDTO = mapperPresupuesto.toDTO(presupuestoActual);

           logger.info("Presupuesto actual del usuario ID {} recuperado: {}", perfilId, presupuestoDTO);

           return ResponseEntity.ok(presupuestoDTO);

       } catch (Exception e) {
           logger.error("Error al obtener el presupuesto actual para el usuario ID {}: {}", perfilId, e.getMessage());
           return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el presupuesto.");
       }
   }
}
