package com.example.Gestor.Model.Repository;

import com.example.Gestor.Model.Entity.Movimientos;
import com.example.Gestor.Model.Entity.Tipo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IRepositoryTransaccion extends CrudRepository<Movimientos, Long> {

    // Listar todas las transacciones de un usuario
    //@Query("SELECT t FROM Movimientos t WHERE t.perfil = :idUsuario")
    List<Movimientos> findAllByUsuario(long idUsuario);

    // Filtrar transacciones por categoría
    //@Query("SELECT t FROM Movimientos t WHERE t.perfil = :idUsuario AND t.categoria.id = :categoriaId")
    List<Movimientos> filterByCategoria(long idUsuario, Tipo tipo);

    /*@Query("SELECT COALESCE(SUM(t.cantidad), 0) " +
            "FROM Movimientos t " +
            "INNER JOIN t.perfil per " +
            "INNER JOIN t.categoria c " +  // Relación con Categoría
            "WHERE per.id = ?1 AND c.tipo = 1")*/
    BigDecimal obtenerTotalIngresos(long id);

    /*@Query("SELECT COALESCE(SUM(t.cantidad), 0) " +
            "FROM Movimientos t " +
            "INNER JOIN t.perfil per " +
            "INNER JOIN t.categoria c " +  // Relación con Categoría
            "WHERE per.id = ?1 AND c.tipo = 2")*/
    BigDecimal obtenerTotalGastos(long id);
}
