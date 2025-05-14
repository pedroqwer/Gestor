package com.example.Gestor.Model.Service.Transaccion;

import com.example.Gestor.DTO.Transaccion.TransaccionDTO;
import com.example.Gestor.Exception.CreateEntityException;
import com.example.Gestor.Exception.NotFoundEntityException;
import com.example.Gestor.Exception.UpdateEntityException;
import com.example.Gestor.Model.Entity.*;
import com.example.Gestor.Model.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TransaccionService implements ITransaccionService {

    @Autowired
    private IRepositoryTransaccion repositoryTransaccion;

    @Autowired
    private IRepositoryPerfil perfilRepository;

    @Autowired
    private IRepositoryCuentaBancaria cuentaBancariaRepository;

    @Autowired
    private IAuditoriaRepository auditoriaRepository;
    Auditoria auditoria = new Auditoria();

    @Override
    public Movimientos findById(long id) {
        return repositoryTransaccion.findById(id).orElseThrow(
                () -> new NotFoundEntityException(id,Movimientos.class.getSimpleName()));
    }

    @Override
    public boolean Create(long id_User, double cantidad, String descripcion, Tipo tipo) {
        try {
            // Validar la cantidad
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
            }

            // Buscar el perfil del usuario en la base de datos
            Perfil perfil = perfilRepository.findById(id_User)
                    .orElseThrow(() -> new NotFoundEntityException(id_User, Perfil.class.getSimpleName()));

            // Obtener la cuenta bancaria del usuario
            CuentaBancaria cuentaBancaria = cuentaBancariaRepository.findByUsuario(perfil.getId())
                    .orElseThrow(() -> new IllegalStateException("El usuario no tiene una cuenta bancaria y no puede realizar transacciones."));

            // Validar y actualizar el saldo
            BigDecimal saldoActual = cuentaBancaria.getSaldo();
            BigDecimal cantidadBD = BigDecimal.valueOf(cantidad);

            if (tipo == Tipo.GASTO) {
                if (saldoActual.compareTo(cantidadBD) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente para realizar esta transacción.");
                }
                cuentaBancaria.setSaldo(saldoActual.subtract(cantidadBD));
            } else if (tipo == Tipo.INGRESO) {
                cuentaBancaria.setSaldo(saldoActual.add(cantidadBD));
            }

            // Guardar el cambio en la cuenta
            cuentaBancariaRepository.save(cuentaBancaria);

            // Crear la transacción
            Movimientos transaccion = new Movimientos();
            transaccion.setPerfil(perfil);
            transaccion.setCantidad(cantidad);
            transaccion.setDescripcion(descripcion);
            transaccion.setFecha(LocalDateTime.now());
            transaccion.setTipo(tipo);

            repositoryTransaccion.save(transaccion);

            // Guardar auditoría
            auditoria.setPerfil(perfil);
            auditoria.setAccion("Creación movimiento");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("El usuario " + perfil.getNombre() + " ha creado una transacción de tipo " + tipo + ".");

            auditoriaRepository.save(auditoria);

            return true;
        } catch (Exception e) {
            throw new CreateEntityException("Error al crear la transacción: " + e.getMessage(), e);
        }
    }

    @Override
    public Movimientos Update(long id, TransaccionDTO transaccion) {
        Movimientos transacciones = repositoryTransaccion.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(id, Movimientos.class.getSimpleName()));

        // Obtener cuenta bancaria asociada
        CuentaBancaria cuentaBancaria = cuentaBancariaRepository.findByUsuario(transacciones.getPerfil().getId())
                .orElseThrow(() -> new NotFoundEntityException(transacciones.getPerfil().getId(), CuentaBancaria.class.getSimpleName()));

        BigDecimal cantidadAntigua = BigDecimal.valueOf(transacciones.getCantidad());
        BigDecimal cantidadNueva = BigDecimal.valueOf(transaccion.getCantidad());
        BigDecimal diferencia = cantidadNueva.subtract(cantidadAntigua);

        // Actualizar el saldo según el tipo (considerando que el tipo puede cambiar)
        if (transacciones.getTipo() == Tipo.GASTO) {
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().add(cantidadAntigua)); // Revertir el gasto original
        } else {
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().subtract(cantidadAntigua)); // Revertir ingreso original
        }

        if (transaccion.getTipo() == Tipo.GASTO) {
            if (cuentaBancaria.getSaldo().compareTo(cantidadNueva) < 0) {
                throw new IllegalArgumentException("Saldo insuficiente para la nueva transacción.");
            }
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().subtract(cantidadNueva));
        } else {
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().add(cantidadNueva));
        }

        cuentaBancariaRepository.save(cuentaBancaria);

        // Actualizar los campos de la transacción
        transacciones.setDescripcion(transaccion.getDescripcion());
        transacciones.setCantidad(transaccion.getCantidad());
        transacciones.setTipo(transaccion.getTipo());

        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setPerfil(transacciones.getPerfil());
            auditoria.setAccion("Update movimiento");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("Transacción actualizada. ID: " + id +
                    ", Cantidad anterior: " + cantidadAntigua +
                    ", Cantidad nueva: " + cantidadNueva +
                    ", Tipo anterior: " + transacciones.getTipo() +
                    ", Tipo nuevo: " + transaccion.getTipo());

            auditoriaRepository.save(auditoria);

            return repositoryTransaccion.save(transacciones);
        } catch (Exception e) {
            throw new UpdateEntityException("Error al modificar la transacción con id: " + id, e);
        }
    }


    /*@Override
    public boolean Create(long id_User, double cantidad, String descripcion, String descripcionCategoria, Tipo tipo) {
        try {
            List<Categorias> categorias = (List<Categorias>) categoriaRepository.findAll(); // Obtener todas las categorías de la base de datos

            // Buscar el perfil del usuario en la base de datos
            Perfil perfil = perfilRepository.findById(id_User)
                    .orElseThrow(() -> new NotFoundEntityException(id_User, Perfil.class.getSimpleName()));

            Optional<CuentaBancaria> cuentaOptional = cuentaBancariaRepository.findByUsuario(perfil.getId());
            if (cuentaOptional.isEmpty()) {
                throw new IllegalStateException("El usuario no tiene una cuenta bancaria y no puede realizar transacciones.");
            }

            // Validar la cantidad
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
            }

            Categorias categoriaExistente = null;

            // Buscar si la categoría ya existe en la lista obtenida
            for (Categorias c : categorias) {
                if (c.getDescripcion().equalsIgnoreCase(descripcionCategoria) && c.getTipo() == tipo) {
                    categoriaExistente = c;
                    break;
                }
            }

            // Si la categoría no existe, se crea una nueva
            if (categoriaExistente == null) {
                categoriaExistente = new Categorias();
                categoriaExistente.setTipo(tipo);
                categoriaExistente.setDescripcion(descripcionCategoria);
                categoriaExistente = categoriaRepository.save(categoriaExistente);
            }

            // Crear la transacción
            Movimientos transaccion = new Movimientos();
            transaccion.setPerfil(perfil);
            transaccion.setCantidad(cantidad);
            transaccion.setDescripcion(descripcion);
            transaccion.setFecha(LocalDateTime.now());
            transaccion.setCategoria(categoriaExistente);

            // Obtener la cuenta bancaria asociada al usuario
            CuentaBancaria cuentaBancaria = cuentaBancariaRepository.findByUsuario(perfil.getId())
                    .orElseThrow(() -> new NotFoundEntityException(perfil.getId(), CuentaBancaria.class.getSimpleName()));

            // Dependiendo del tipo de transacción (ingreso o gasto), actualizar el saldo de la cuenta bancaria
            if (tipo == Tipo.GASTO) {
                BigDecimal saldoActual = cuentaBancaria.getSaldo();
                BigDecimal cantidadTransaccion = BigDecimal.valueOf(cantidad);

                if (saldoActual.compareTo(cantidadTransaccion) < 0) {
                    throw new IllegalArgumentException("Saldo insuficiente para realizar esta transacción.");
                }
                // Restar la cantidad al saldo de la cuenta bancaria
                cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().subtract(BigDecimal.valueOf(cantidad)));
            } else if (tipo == Tipo.INGRESO) {
                // Sumar la cantidad al saldo de la cuenta bancaria
                cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().add(BigDecimal.valueOf(cantidad)));
            }

            // Guardar la cuenta bancaria actualizada
            cuentaBancariaRepository.save(cuentaBancaria);

            // Guardar la transacción y verificar si se guardó correctamente
            repositoryTransaccion.save(transaccion);

            auditoria.setPerfil(perfil);
            auditoria.setAccion("CREACION");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("El usuario " + perfil.getNombre() + " ha creado un presupuesto.");

            auditoriaRepository.save(auditoria);

            return true;
        }catch (Exception e) {
            throw new CreateEntityException("Error al crear la transaccion: " + e.getMessage(), e);
        }
    }

    @Override
    public Movimientos Update(long id, TransaccionDTO transaccion) {
        Movimientos transacciones = repositoryTransaccion.findById(id).orElseThrow(
                () -> new NotFoundEntityException(id, Movimientos.class.getSimpleName())
        );

        // Obtener la categoría actual
        Categorias categoriaActual = transacciones.getCategoria();
        Categorias nuevaCategoria = categoriaActual; // Se mantiene la misma por defecto

        // Si la descripción de la categoría cambia, crear una nueva
        if (!categoriaActual.getDescripcion().equals(transaccion.getDescripcion())) {
            List<Categorias> todasLasCategorias = (List<Categorias>) categoriaRepository.findAll(); // Obtener todas las categorías

            Optional<Categorias> categoriaExistente = todasLasCategorias.stream()
                    .filter(cat -> cat.getDescripcion().equals(transaccion.getDescripcion()))
                    .findFirst();
            if (categoriaExistente.isPresent()) {
                nuevaCategoria = categoriaExistente.get();
            } else {
                nuevaCategoria = new Categorias();
                nuevaCategoria.setDescripcion(transaccion.getDescripcion());
                nuevaCategoria.setTipo(categoriaActual.getTipo()); // Mantener el tipo original
                nuevaCategoria = categoriaRepository.save(nuevaCategoria); // Guardar y obtener el objeto persistido
            }
        }

        // Obtener la cuenta bancaria asociada al usuario
        CuentaBancaria cuentaBancaria = cuentaBancariaRepository.findByUsuario(transacciones.getPerfil().getId())
                .orElseThrow(() -> new NotFoundEntityException(transacciones.getPerfil().getId(), CuentaBancaria.class.getSimpleName()));

        // Calcular la diferencia entre la cantidad nueva y la anterior
        BigDecimal cantidadAntigua = BigDecimal.valueOf(transacciones.getCantidad());
        BigDecimal cantidadNueva = BigDecimal.valueOf(transaccion.getCantidad());
        BigDecimal diferencia = cantidadNueva.subtract(cantidadAntigua);

        // Ajustar el saldo de la cuenta bancaria
        if (categoriaActual.getTipo() == Tipo.GASTO) {
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().subtract(diferencia)); // Restar si es un gasto
        } else if (categoriaActual.getTipo() == Tipo.INGRESO) {
            cuentaBancaria.setSaldo(cuentaBancaria.getSaldo().add(diferencia)); // Sumar si es un ingreso
        }

        // Guardar los cambios en la cuenta bancaria
        cuentaBancariaRepository.save(cuentaBancaria);

        transacciones.setDescripcion(transaccion.getDescripcion());
        transacciones.setCategoria(nuevaCategoria);
        transacciones.setCantidad(transaccion.getCantidad()); // Actualizar la cantidad en la transacción

        try {

            Auditoria auditoria = new Auditoria();
            auditoria.setPerfil(transacciones.getPerfil());  // ID del perfil (usuario que realiza la acción)
            auditoria.setAccion("UPDATE");
            auditoria.setFecha(LocalDateTime.now());
            auditoria.setDetalles("Transacción actualizada. ID: " + id +
                    ", Descripción anterior: " + transacciones.getDescripcion() +
                    ", Descripción nueva: " + transaccion.getDescripcion() +
                    ", Categoría anterior: " + categoriaActual.getDescripcion() +
                    ", Categoría nueva: " + nuevaCategoria.getDescripcion() +
                    ", Cantidad anterior: " + cantidadAntigua +
                    ", Cantidad nueva: " + cantidadNueva +
                    ", Diferencia: " + diferencia);

            // Guardar la auditoría en la base de datos (asumiendo que tienes un repositorio para 'auditoria')
            auditoriaRepository.save(auditoria);

            return repositoryTransaccion.save(transacciones);
        } catch (Exception e) {
            throw new UpdateEntityException("Error al modificar la transacción con id: " + id, e);
        }
    }*/

    @Override
    public List<Movimientos> findAllByUsuario(long idUsuario) {
        try {
            return (List<Movimientos>) repositoryTransaccion.findAllByUsuario(idUsuario);
        } catch (Exception e) {
           throw new NotFoundEntityException(idUsuario, Movimientos.class.getSimpleName());
        }
    }

    @Override
    public List<Movimientos> filterByCategoria(long idUsuario, Tipo tipo) {
        try {
            return (List<Movimientos>) repositoryTransaccion.filterByCategoria(idUsuario, tipo);
        } catch (Exception e) {
            //throw new NotFoundEntityException(idUsuario, Movimientos.class.getSimpleName());
            System.out.println("Error "+e.getMessage());
        }
        return List.of();
    }

    @Override
    public BigDecimal obtenerTotalIngresos(long id) {
        try {
            return repositoryTransaccion.obtenerTotalIngresos(id);
        } catch (Exception e) {
            throw new NotFoundEntityException(id, Movimientos.class.getSimpleName());
        }
    }

    @Override
    public BigDecimal obtenerTotalGastos(long id) {
        try {
            return repositoryTransaccion.obtenerTotalGastos(id);
        } catch (Exception e) {
            throw new NotFoundEntityException(id, Movimientos.class.getSimpleName());
        }
    }

}
