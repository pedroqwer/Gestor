package com.example.Gestor.Controller.Autentication;

import com.example.Gestor.DTO.Usuario.CrearUsuarioDTO;
import com.example.Gestor.DTO.Usuario.LoginUsuarioDTO;
import com.example.Gestor.Exception.Response;
import com.example.Gestor.Mappers.MapperClas;
import com.example.Gestor.Mappers.MapperPerfil.MApperPerfil;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Entity.Rol;
import com.example.Gestor.Model.Repository.IUsuarioRepository.IRolRepository;
import com.example.Gestor.Model.Service.Authentication.IAuthenticationService;
import com.example.Gestor.Model.Service.Perfil.IPerfilService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"*"})
@Tag(name = "Authentication", description = "Gestión de Authentication")
public class AuthenticationController {

    @Autowired
    private IAuthenticationService authenticationService;

    @Autowired
    private MapperClas mapper;

    @Autowired
    private IRolRepository roleRepository;

    /**
     * Registro de un nuevo perfil de usuario.
     *
     * @param registerUsuarioDto DTO con la información del usuario a registrar.
     * @return ResponseEntity con mensaje de éxito o error.
     */
    @PostMapping("/signup/perfil")
    @Operation(summary = "Registro de perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
            @ApiResponse(responseCode = "401", description = "No autorizado"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
            @ApiResponse(responseCode = "409", description = "Conflicto: el usuario ya existe"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> registerPerfil(@RequestBody CrearUsuarioDTO registerUsuarioDto) {
        try {
            Perfil user = mapper.mapType(registerUsuarioDto, Perfil.class);
            List<Rol> roles = roleRepository.findByNombreIn(registerUsuarioDto.getRoles());
            user.setRoles(roles);
            authenticationService.signup(user);

            // Devuelve un JSON con mensaje
            return ResponseEntity.ok(Map.of("mensaje", "Perfil registrado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("mensaje", "Error al registrar el perfil: " + e.getMessage()));
        }
    }


    /**
     * Autenticación de un usuario en el sistema.
     *
     * @param loginUserDto DTO con las credenciales del usuario.
     * @return ResponseEntity con el token JWT generado si la autenticación es exitosa.
     */
    @PostMapping("/login")
    @Operation(summary = "Login de perfil")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso, devuelve el token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<?> authenticate(@RequestBody LoginUsuarioDTO loginUserDto) {
        try {
            Perfil loginUser = mapper.mapType(loginUserDto, Perfil.class);
            String jwtToken = authenticationService.authenticate(loginUser);

            return ResponseEntity.ok().body(Map.of("token", jwtToken));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensaje", "Credenciales inválidas"));
        }
    }
}
