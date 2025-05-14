package com.example.Gestor.Exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@Data
public class Response {
    private int code;
    private String message;
    private Object data;
    private Map<String, String> errors;

    // ✅ Constructor privado para evitar instanciación directa
    private Response(int code, String message, Object data, Map<String, String> errors) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.errors = errors != null ? errors : new HashMap<>();
    }

    // ✅ Respuesta exitosa sin datos
    public static Response ok(String message) {
        return new Response(HttpStatus.OK.value(), message, null, null);
    }

    // ✅ Respuesta exitosa con datos
    public static Response ok(String message, Object data) {
        return new Response(HttpStatus.OK.value(), message, data, null);
    }

    // ✅ Error de validación con lista de errores
    public static Response validationError(Map<String, String> errors) {
        return new Response(HttpStatus.BAD_REQUEST.value(), "Error de validación", null, errors);
    }

    // ✅ Recurso no encontrado
    public static Response notFound(String message) {
        return new Response(HttpStatus.NOT_FOUND.value(), message, null, null);
    }

    // ✅ Error general (500)
    public static Response internalError(String message) {
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null, null);
    }

    // ✅ Error general con detalles
    public static Response internalError(String message, Map<String, String> errors) {
        return new Response(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null, errors);
    }

    // ✅ Método general para errores con código y mensaje personalizados
    public static Response generalError(int code, String message) {
        return new Response(code, message, null, null);
    }

    // ✅ Método general para errores con detalles adicionales
    public static Response generalError(int code, String message, Map<String, String> errors) {
        return new Response(code, message, null, errors);
    }
}
