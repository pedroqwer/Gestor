package com.example.Gestor.Controller.Cuenta;

import com.example.Gestor.DTO.Cuenta.CuentaDTO;
import com.example.Gestor.DTO.Cuenta.NewCuentaDTO;
import com.example.Gestor.DTO.Presupuesto.NewPresupuestoDTO;
import com.example.Gestor.Exception.CuentaNoEncontradaException;
import com.example.Gestor.Exception.Response;
import com.example.Gestor.Mappers.MapperCuenta.MapperCuenta;
import com.example.Gestor.Model.Entity.CuentaBancaria;
import com.example.Gestor.Model.Service.Cuenta.ICuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
@Tag(name = "Cuenta", description = "Gestión de cuentas bancarias")
public class ControllerCuenta {

    @Autowired
    private MapperCuenta mapperCuenta;

    @Autowired
    private ICuentaService iCuentaService;

    private final Logger logger = LoggerFactory.getLogger(ControllerCuenta.class);

    @PutMapping("/crearCuenta")
    @Operation(summary = "Crear cuenta", description = "Crea una nueva cuent con los datos proporcionados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cuenta agregado correctamente"),
            @ApiResponse(responseCode = "500", description = "Error al agregar la cuenta")
    })
    public ResponseEntity<?> crearCuenta(@RequestBody NewCuentaDTO newCuentaDTO) {
        logger.info("Iniciando creación de cuenta para el usuario: {}", newCuentaDTO.getUsuario());

        boolean realizado = iCuentaService.crearCuentaBancaria(
                newCuentaDTO.getUsuario(),
                newCuentaDTO.getSaldo(),
                newCuentaDTO.getMoneda()
        );

        Map<String, Object> response = new HashMap<>();

        if (realizado) {
            response.put("mensaje", "Cuenta agregado correctamente");
            logger.info("Cuenta creado con éxito para el usuario: {}", newCuentaDTO.getUsuario());
            return ResponseEntity.ok(response);
        } else {
            response.put("mensaje", "Error al agregar la cuenta");
            logger.error("Error al crear la cuenta para el usuario: {}", newCuentaDTO.getUsuario());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Obtiene la cuenta bancaria de un usuario dado su ID.
     *
     * @param id Identificador del usuario.
     * @return ResponseEntity con la cuenta bancaria si existe o un mensaje de error.
     */
    @GetMapping("/cuentaUser/{id}")
    @Operation(summary = "Obtener cuenta bancaria", description = "Obtiene la cuenta bancaria por su ID de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> obtenerCuenta(@PathVariable long id) {
        logger.info("Obteniendo cuenta para el usuario con ID: {}", id);
        try {
            // Intentar obtener la cuenta bancaria por el ID del usuario
            CuentaBancaria cuentaBancaria = iCuentaService.obtenerCuentaPorUsuario(id);
            String nombreUsuario = cuentaBancaria.getUsuario().getNombre();

            // Crear el DTO con los detalles de la cuenta
            CuentaDTO cuentaDTO = new CuentaDTO(
                    cuentaBancaria.getId(),
                    nombreUsuario,
                    cuentaBancaria.getSaldo(),
                    cuentaBancaria.getMoneda()
            );

            // Retornar la respuesta con la cuenta
            return ResponseEntity.ok(cuentaDTO);
        } catch (CuentaNoEncontradaException e) {
            // Manejar el caso donde no se encuentra la cuenta
            logger.warn("Cuenta no encontrada para el usuario con ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cuenta no encontrada");
        } catch (Exception e) {
            // Manejar cualquier otro error inesperado
            logger.error("Error inesperado al obtener cuenta: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error interno del servidor");
        }
    }



    /**
     * Consulta el saldo de un usuario dado su ID.
     *
     * @param usuarioId Identificador del usuario.
     * @return ResponseEntity con el saldo actual o un mensaje de error en caso de fallo.
     */
    @GetMapping("/saldo/{usuarioId}")
    @Operation(summary = "Consultar saldo", description = "Consulta el saldo disponible en la cuenta de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Saldo consultado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Saldo no disponible"),
            @ApiResponse(responseCode = "500", description = "Error al consultar el saldo")
    })
    public ResponseEntity<?> consultarSaldo(@PathVariable Long usuarioId) {
        logger.info("Consultando saldo para el usuario con ID: {}", usuarioId);
        try {
            BigDecimal saldo = iCuentaService.consultarSaldo(usuarioId);
            return ResponseEntity.ok("Saldo: "+ saldo);
        } catch (Exception e) {
            logger.error("Error al consultar saldo para usuario con ID: {}. Detalles: {}", usuarioId, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al consultar el saldo");
        }
    }

    @Operation(summary = "Verificar si un usuario tiene cuenta bancaria", description = "Consulta si un usuario tiene una cuenta bancaria asociada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta bancaria asociada encontrada"),
            @ApiResponse(responseCode = "404", description = "No se encontró cuenta bancaria asociada para el usuario"),
            @ApiResponse(responseCode = "500", description = "Error al realizar la consulta")
    })
    @GetMapping("/tiene-cuenta/{usuarioId}")
    public ResponseEntity<Boolean> tieneCuenta(@PathVariable Long usuarioId) {
        boolean tieneCuenta = iCuentaService.tieneCuenta(usuarioId);
        return ResponseEntity.ok(tieneCuenta);
    }
}
