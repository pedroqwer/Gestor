    package com.example.Gestor.DTO.Presupuesto;

    import jakarta.persistence.Column;
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
    public class PresupuestoDTO implements Serializable {

        private long id;
        private Double cantidadLimite;
        private String descripcion;
        private LocalDateTime fecha;
    }
