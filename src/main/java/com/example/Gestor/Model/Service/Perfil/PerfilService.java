package com.example.Gestor.Model.Service.Perfil;

import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.Exception.NotFoundEntityException;
import com.example.Gestor.Exception.UpdateEntityException;
import com.example.Gestor.Model.Entity.Auditoria;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Entity.Usuario;
import com.example.Gestor.Model.Repository.IAuditoriaRepository;
import com.example.Gestor.Model.Repository.IRepositoryPerfil;
import com.example.Gestor.Model.Repository.IUsuarioRepository.IUsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PerfilService implements IPerfilService {

    @Autowired
    private IRepositoryPerfil repositoryPerfil;

    @Autowired
    private PasswordEncoder passwordEncoder; // Inyecta el PasswordEncoder de Spring Security

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Autowired
    private IAuditoriaRepository auditoriaRepository;
    Auditoria auditoria = new Auditoria();

    @Override
    public Perfil Update_By_User(long id, PerfilDTO perfil) {

        Perfil perfil1 = repositoryPerfil.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(id, Perfil.class.getSimpleName()));


        perfil1.setNombre(perfil.getNombre());
        perfil1.setApellido(perfil.getApellido());
        perfil1.setEmail(perfil.getEmail());
        perfil1.setDni(perfil.getDni());
        perfil1.setTelefono(perfil.getTelefono());

        // Registrar la auditoría del inicio de sesión
        auditoria.setPerfil(perfil1);
        auditoria.setAccion("Update perfil");
        auditoria.setFecha(LocalDateTime.now());
        auditoria.setDetalles("El usuario " + perfil.getNombre() + " ha modificado su perfil.");

        auditoriaRepository.save(auditoria);

        try {
            return repositoryPerfil.save(perfil1);
        }catch (Exception e){
            throw new UpdateEntityException("Error al modificar el perfil con id " + id, e);
        }

    }

    @Override
    public List<Perfil> GetAll() {
        return (List<Perfil>) repositoryPerfil.findAll();
    }

    @Override
    public Perfil GetById(long id) {
        return repositoryPerfil.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(id, Perfil.class.getSimpleName()));

    }

    @Override
    public void Delete(long id) {
        try {
            repositoryPerfil.deleteById(id);
        }catch (Exception e){
            throw new NotFoundEntityException(id, Perfil.class.getSimpleName());
        }
    }

    //Admin
    @Override
    public boolean verificarCambioCredenciales(long id, PerfilDTO nuevoPerfil) {
        Perfil perfilExistente = repositoryPerfil.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(id, Perfil.class.getSimpleName()));

        // Verificar si username o password están cambiando
        return !perfilExistente.getUsername().equals(nuevoPerfil.getUsername()) ||
                !perfilExistente.getPassword().equals(nuevoPerfil.getPassword());
    }

    //Para el final
    @Override
    public Long obtenerIdByUsernameAndPass(String username, String password) {
        try {
            Optional<Usuario> usuario = usuarioRepository.findByUsername(username);

            if (usuario.isPresent() && passwordEncoder.matches(password, usuario.get().getPassword())) {
                return usuario.get().getId();
            } else {
                throw new IllegalStateException("Usuario no encontrado o contraseña incorrecta.");
            }

        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuario: " + e.getMessage());
        }
    }

    @Override
    public String obtenertelefonobyid(long id) {
        return repositoryPerfil.obtenertelefonobyid(id);
    }


}
