package com.example.Gestor.Tests.CuentaTest;

import com.example.Gestor.Controller.Cuenta.ControllerCuenta;
import com.example.Gestor.DTO.Cuenta.CuentaDTO;
import com.example.Gestor.DTO.Cuenta.NewCuentaDTO;
import com.example.Gestor.Exception.CuentaNoEncontradaException;
import com.example.Gestor.Mappers.MapperCuenta.MapperCuenta;
import com.example.Gestor.Model.Entity.CuentaBancaria;
import com.example.Gestor.Model.Entity.Perfil;
import com.example.Gestor.Model.Service.Cuenta.ICuentaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ControllerCuentaTest {

    @InjectMocks
    private ControllerCuenta controllerCuenta;

    @Mock
    private ICuentaService iCuentaService;

    @Mock
    private MapperCuenta mapperCuenta;

    private CuentaBancaria cuentaBancaria;
    private CuentaDTO cuentaDTO;

    @BeforeEach
    void setUp() {
        cuentaBancaria = new CuentaBancaria();
        cuentaBancaria.setId(1L);
        cuentaBancaria.setSaldo(BigDecimal.valueOf(1000));

        cuentaDTO = new CuentaDTO();
        cuentaDTO.setSaldo(BigDecimal.valueOf(1000));
    }

    // ✅ Test para obtener cuenta por usuario
    @Test
    void obtenerCuenta_UsuarioExistente_DebeRetornarCuenta() {
        long userId = 2L;

        // Crear y configurar la cuenta bancaria con un usuario asignado
        CuentaBancaria cuentaBancaria = new CuentaBancaria();
        cuentaBancaria.setId(2L);
        cuentaBancaria.setSaldo(BigDecimal.valueOf(1000));
        cuentaBancaria.setMoneda("USD");

        // Crear y configurar el usuario (Perfil)
        Perfil usuario = new Perfil();
        usuario.setNombre("Juan");
        usuario.setApellido("Perez");
        cuentaBancaria.setUsuario(usuario);

        // Configuración del mock
        when(iCuentaService.obtenerCuentaPorUsuario(userId)).thenReturn(cuentaBancaria);

        // Llamar al método del controlador
        ResponseEntity<?> response = controllerCuenta.obtenerCuenta(userId);

        // Verificar que el código de estado sea 200 OK
        assertEquals(200, response.getStatusCodeValue());

        // Verificar que el cuerpo de la respuesta no sea null
        assertNotNull(response.getBody());

        // Verificar que el servicio haya sido llamado una vez
        verify(iCuentaService, times(1)).obtenerCuentaPorUsuario(userId);
    }

    // ❌ Test para obtener cuenta de usuario que no existe
    @Test
    void obtenerCuenta_UsuarioNoExiste_DebeRetornarError() {
        long userId = 999L;
        when(iCuentaService.obtenerCuentaPorUsuario(userId)).thenThrow(new CuentaNoEncontradaException("Cuenta no encontrada"));

        ResponseEntity<?> response = controllerCuenta.obtenerCuenta(userId);

        assertEquals(404, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Cuenta no encontrada"));
        verify(iCuentaService, times(1)).obtenerCuentaPorUsuario(userId);
    }


    // ✅ Test para consultar saldo de usuario
    @Test
    void consultarSaldo_UsuarioExistente_DebeRetornarSaldo() {
        long userId = 1L;
        BigDecimal saldoEsperado = BigDecimal.valueOf(1000);

        // Configurar el comportamiento del mock
        when(iCuentaService.consultarSaldo(userId)).thenReturn(saldoEsperado);

        // Llamar al método del controlador
        ResponseEntity<?> response = controllerCuenta.consultarSaldo(userId);

        // Verificar que el código de estado sea 200 OK
        assertEquals(200, response.getStatusCodeValue());

        // Verificar que el cuerpo de la respuesta contenga el mensaje esperado
        assertTrue(response.getBody().toString().contains("Saldo: " + saldoEsperado));

        // Verificar que el servicio haya sido llamado una vez
        verify(iCuentaService, times(1)).consultarSaldo(userId);
    }



    @Test
    void crearCuenta_Exitoso_DebeRetornarOk() {
        NewCuentaDTO newCuentaDTO = new NewCuentaDTO(1L, BigDecimal.valueOf(500), "USD");

        when(iCuentaService.crearCuentaBancaria(
                newCuentaDTO.getUsuario(),
                newCuentaDTO.getSaldo(),
                newCuentaDTO.getMoneda()
        )).thenReturn(true);

        ResponseEntity<?> response = controllerCuenta.crearCuenta(newCuentaDTO);

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Cuenta agregado correctamente"));
        verify(iCuentaService, times(1)).crearCuentaBancaria(1L, BigDecimal.valueOf(500), "USD");
    }

    @Test
    void crearCuenta_Fallo_DebeRetornarError() {
        NewCuentaDTO newCuentaDTO = new NewCuentaDTO(1L, BigDecimal.valueOf(500), "USD");

        when(iCuentaService.crearCuentaBancaria(
                newCuentaDTO.getUsuario(),
                newCuentaDTO.getSaldo(),
                newCuentaDTO.getMoneda()
        )).thenReturn(false);

        ResponseEntity<?> response = controllerCuenta.crearCuenta(newCuentaDTO);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al agregar la cuenta"));
        verify(iCuentaService, times(1)).crearCuentaBancaria(1L, BigDecimal.valueOf(500), "USD");
    }


    @Test
    void consultarSaldo_Error_DebeRetornarInternalServerError() {
        long userId = 99L;
        when(iCuentaService.consultarSaldo(userId)).thenThrow(new RuntimeException("Error en la base de datos"));

        ResponseEntity<?> response = controllerCuenta.consultarSaldo(userId);

        assertEquals(500, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Error al consultar el saldo"));
        verify(iCuentaService, times(1)).consultarSaldo(userId);
    }

    // ✅ Test para verificar si un usuario tiene cuenta bancaria
    @Test
    void tieneCuenta_UsuarioConCuenta_DebeRetornarTrue() {
        long userId = 1L;

        // Configurar el mock para devolver true
        when(iCuentaService.tieneCuenta(userId)).thenReturn(true);

        // Llamar al método del controlador
        ResponseEntity<Boolean> response = controllerCuenta.tieneCuenta(userId);

        // Verificar que el código de estado sea 200 OK
        assertEquals(200, response.getStatusCodeValue());

        // Verificar que el cuerpo de la respuesta sea true
        assertTrue(response.getBody());

        // Verificar que el servicio haya sido llamado una vez
        verify(iCuentaService, times(1)).tieneCuenta(userId);
    }

    @Test
    void tieneCuenta_UsuarioSinCuenta_DebeRetornarFalse() {
        long userId = 2L;

        // Configurar el mock para devolver false
        when(iCuentaService.tieneCuenta(userId)).thenReturn(false);

        // Llamar al método del controlador
        ResponseEntity<Boolean> response = controllerCuenta.tieneCuenta(userId);

        // Verificar que el código de estado sea 200 OK
        assertEquals(200, response.getStatusCodeValue());

        // Verificar que el cuerpo de la respuesta sea false
        assertFalse(response.getBody());

        // Verificar que el servicio haya sido llamado una vez
        verify(iCuentaService, times(1)).tieneCuenta(userId);
    }

}