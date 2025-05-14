package com.example.Gestor.DTO.Auditroria;

import com.example.Gestor.Model.Entity.Perfil;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuditoriaDTO implements Serializable {

    private Long id;
    private String nombreUsuario;
    private String accion;
    private LocalDateTime fecha;
    private String detalles;

    @Override
    public String toString() {
        return "Auditoria{" +
                "nombreUsuario='" + nombreUsuario + '\'' +
                ", accion='" + accion + '\'' +
                ", fecha=" + fecha +
                ", detalles='" + detalles + '\'' +
                '}';
    }
}
