package com.example.Gestor.Controller.Perfil;

import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.DTO.Usuario.LoginUsuarioDTO;
import com.example.Gestor.Exception.Response;
import com.example.Gestor.Mappers.MapperPerfil.MApperPerfil;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Service.Perfil.IPerfilService;
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

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"*"})
@Tag(name = "Perfil", description = "Gestión de Perfiles de Usuario")
public class ControllerPerfil {

    @Autowired
    private IPerfilService perfilService;

    @Autowired
    private MApperPerfil mapperPerfil;

    private final Logger logger = LoggerFactory.getLogger(ControllerPerfil.class);

    @GetMapping("/perfilid/{username}/{password}")
    @Operation(summary = "Obtener ID del usuario por username y password",
            description = "Este endpoint devuelve el ID de usuario al validar las credenciales proporcionadas.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ID del usuario encontrado y retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> obtenerIdByUsernameAndPass(
            @PathVariable String username,
            @PathVariable String password) {

        Long userId = perfilService.obtenerIdByUsernameAndPass(username, password);

        if (userId != null) {
            return ResponseEntity.ok(userId);
        } else {
            return ResponseEntity.status(401).body("Credenciales inválidas");
        }
    }


    @GetMapping("/telefonouser/{id}")
    @Operation(summary = "Obtener teléfono del usuario por ID",
            description = "Devuelve el número de teléfono asociado al perfil del usuario por su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Teléfono encontrado y retornado"),
            @ApiResponse(responseCode = "404", description = "Teléfono no encontrado para el perfil"),
            @ApiResponse(responseCode = "500", description = "Error interno al intentar obtener el teléfono")
    })
    public ResponseEntity<?> obtenerTelefonoPorId(@PathVariable long id) {
        try {
            // Log para indicar que se está buscando el teléfono de un perfil
            logger.info("Iniciando la búsqueda del teléfono para el perfil con ID: {}", id);

            // Obtener el teléfono del perfil
            String telefono = perfilService.obtenertelefonobyid(id);

            if (telefono == null || telefono.isEmpty()) {
                // Si no se encuentra el teléfono o está vacío, log de advertencia y devolver 404
                logger.warn("No se encontró un teléfono para el perfil con ID: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Teléfono no encontrado para el perfil con ID: " + id);
            }

            // Si el teléfono se encuentra, devolver 200 con el teléfono
            logger.info("Teléfono encontrado para el perfil con ID: {}: {}", id, telefono);
            return ResponseEntity.ok(telefono);

        } catch (Exception e) {
            // Log de error si ocurre un problema al obtener el teléfono
            logger.error("Error al obtener el teléfono para el perfil con ID: {}: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener el teléfono.");
        }
    }

    /**
     * Modifica el perfil de un usuario.
     */
    @PutMapping("/Perfil_Modificar_User/{id}")
    @Operation(summary = "Modificar perfil de usuario", description = "Actualiza la información del perfil de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil modificado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> modificarPerfil(@PathVariable long id, @RequestBody PerfilDTO newPerfil) {
        logger.info("Modificando perfil del usuario con ID: {}", id);
        try {
            Perfil perfil = perfilService.Update_By_User(id, newPerfil);
            return ResponseEntity.ok("Perfil modificado con éxito");
        } catch (Exception e) {
            logger.error("Error al modificar perfil con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.internalError("Error al modificar el perfil"));
        }
    }

    ///Admin
    /**
     * Modifica el perfil de un usuario con privilegios de administrador.

    @PutMapping("/Perfil_Modificar_Admin/{id}")
    @Operation(summary = "Modificar perfil (Admin)", description = "Permite a un administrador modificar el perfil de un usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil modificado con éxito"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> modificarPerfil_Admin(@PathVariable long id, @RequestBody Perfil_Admin_DTO newPerfil) {
        logger.info("Modificando perfil por admin con ID: {}", id);
        try {
            // Verificar si se están cambiando credenciales
            boolean credencialesModificadas = perfilService.verificarCambioCredenciales(id, newPerfil);

            Perfil perfil = perfilService.Update_By_Admin(id, newPerfil);

            Map<String, Object> response = new HashMap<>();

            if (credencialesModificadas) {
                response.put("aviso", "Se modificaron las credenciales. El usuario debe volver a iniciar sesión.");
            }

            return ResponseEntity.ok("Perfil modificado con éxito");
        } catch (Exception e) {
            logger.error("Error al modificar perfil por admin con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.internalError("Error al modificar el perfil"));
        }
    }
    */
    /**
     * Obtiene la lista de todos los perfiles registrados.
     */
    @GetMapping("/listar")
    @Operation(summary = "Listar perfiles", description = "Obtiene una lista de todos los perfiles registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de perfiles obtenida con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> obtenerPerfiles() {
        logger.info("Obteniendo lista de perfiles...");
        try {
            List<Perfil> perfiles = perfilService.GetAll();
            List<PerfilDTO> perfilDTOS = mapperPerfil.toDTOL(perfiles);
            return ResponseEntity.ok(Response.ok("Perfiles obtenidos exitosamente", perfilDTOS));
        } catch (Exception e) {
            logger.error("Error al obtener perfiles. Detalles: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.internalError("Error al obtener los perfiles"));
        }
    }

    /**
     * Obtiene un perfil específico dado su ID.
     */
    @GetMapping("/perfil/{id}")
    @Operation(summary = "Obtener perfil por ID", description = "Busca y devuelve un perfil específico dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil obtenido con éxito"),
            @ApiResponse(responseCode = "404", description = "Perfil no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> obtenerPerfil(@PathVariable long id) {
        logger.info("Obteniendo perfil con ID: {}", id);
        try {
            Perfil perfil = perfilService.GetById(id);
            PerfilDTO perfilDTO = mapperPerfil.toDTO(perfil);
            return ResponseEntity.ok(Response.ok("Perfil obtenido con éxito", perfilDTO));
        } catch (Exception e) {
            logger.error("Error al obtener perfil con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Response.notFound("Perfil no encontrado"));
        }
    }

    /**
     * Elimina un perfil dado su ID.
     */
    @DeleteMapping("/borrar/{id}")
    @Operation(summary = "Eliminar perfil", description = "Elimina un perfil de usuario dado su ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil eliminado con éxito"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> deletePerfil(@PathVariable long id) {
        logger.info("Eliminando perfil con ID: {}", id);
        try {
            perfilService.Delete(id);
            return ResponseEntity.ok("Perfil eliminado con éxito");
        } catch (Exception e) {
            logger.error("Error al eliminar perfil con ID: {}. Detalles: {}", id, e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Response.internalError("Error al eliminar el perfil"));
        }
    }
}
