package com.example.Gestor.Model.Service.Perfil;

import com.example.Gestor.DTO.Perfil.PerfilDTO;
import com.example.Gestor.Model.Entity.Perfil;

import java.util.List;

public interface IPerfilService {
       //User
       Perfil Update_By_User(long id, PerfilDTO perfil);
       boolean verificarCambioCredenciales(long id, PerfilDTO nuevoPerfil);
       Long obtenerIdByUsernameAndPass(String username, String password);
       String obtenertelefonobyid(long id);
       //Admin
       List<Perfil> GetAll();
       Perfil GetById(long id);
       void Delete(long id);
}
