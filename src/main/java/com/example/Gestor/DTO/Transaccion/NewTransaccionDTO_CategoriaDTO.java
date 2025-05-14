package com.example.Gestor.DTO.Transaccion;

import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Entity.Tipo;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NewTransaccionDTO_CategoriaDTO implements Serializable {

    long id_User;
    double cantidad;
    String descripcion;
    //String descripcionCategoria;
    //String presupuestomes;
    Tipo tipo;
}
