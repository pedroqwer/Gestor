package com.example.Gestor.Controller.Auditoria;

import com.example.Gestor.Controller.Cuenta.ControllerCuenta;
import com.example.Gestor.DTO.Auditroria.AuditoriaDTO;
import com.example.Gestor.DTO.Cuenta.CuentaDTO;
import com.example.Gestor.DTO.Presupuesto.PresupuestoDTO;
import com.example.Gestor.Exception.Response;
import com.example.Gestor.Mappers.MapperAuditoria.MapperAuditoria;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.CuentaBancaria;
import com.example.Gestor.Model.Entity.Presupuesto;
import com.example.Gestor.Model.Service.Auditoria.IAuditoriaService;
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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
@Tag(name = "Auditoria", description = "Gestión de auditoria")
public class ControllerAuditoria {

    @Autowired
    private MapperAuditoria mapperAuditoria;

    @Autowired
    private IAuditoriaService auditoriaService;

    private final Logger logger = LoggerFactory.getLogger(ControllerAuditoria.class);

    @GetMapping("/auditoria/{id}")
    @Operation(summary = "Obtener auditoria", description = "Obtiene la auditoria por su ID de un usuario .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Auditoria obtenida exitosamente"),
            @ApiResponse(responseCode = "404", description = "Auditoria no encontrada"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> obtenerAuditoria(@PathVariable long id) {
        logger.info("Obteniendo auditoria para el usuario con ID: {}", id);
        try {
            Auditoria auditoria = auditoriaService.findById(id);

            // Obtener el nombre del usuario
            String nombreUsuario = auditoria.getPerfil().getNombre();


            // Mapear a DTO con el nombre del usuario
            AuditoriaDTO auditoriaDTO = new AuditoriaDTO(
                    auditoria.getId(),
                    nombreUsuario,
                    auditoria.getAccion(),
                    auditoria.getFecha(),
                    auditoria.getDetalles()
            );

            return ResponseEntity.ok("Auditoria: " + auditoriaDTO.toString());
        } catch (Exception e) {
            logger.error("Error al obtener la auditoria del usuario con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Auditoria no encontrada");
        }
    }

    @GetMapping("/usuarioauditrorias/{idUser}")
    @Operation(
            summary = "Obtener todas las auditorías de un usuario",
            description = "Recupera la lista de todas las auditorías asociadas a un usuario dado su ID."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Auditorías encontradas"),
            @ApiResponse(responseCode = "204", description = "No se encontraron auditorías")
    })
    public ResponseEntity<?> getAllAuditroriasByUser(@PathVariable long idUser) {
        logger.info("Obteniendo todas las auditorías del usuario ID: {}", idUser);
        List<Auditoria> auditorias = auditoriaService.findAllByUserAuditoria(idUser);

        if (auditorias.isEmpty()) {
            logger.warn("No se encontraron auditorías para el usuario ID: {}", idUser);
            return ResponseEntity.ok("No se encontraron auditorías");
        }

        List<AuditoriaDTO> auditoriaDTOS = auditorias.stream().map(a -> {
            String nombreUsuario = a.getPerfil().getNombre();
            return new AuditoriaDTO(
                    a.getId(),
                    nombreUsuario,
                    a.getAccion(),
                    a.getFecha(),
                    a.getDetalles()
            );
        }).collect(Collectors.toList());

        logger.info("Auditorías encontradas para el usuario ID: {}", idUser);
        return ResponseEntity.ok(auditoriaDTOS);
    }

}
