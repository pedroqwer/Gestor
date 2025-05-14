package com.example.Gestor.Exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    /**
     * Maneja errores de validación de argumentos en los controladores.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            if (error instanceof FieldError fieldError) {
                errors.put(fieldError.getField(), error.getDefaultMessage());
            }
        });

        logger.warn("Error de validación: {}", errors);
        return new ResponseEntity<>(Response.validationError(errors), HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja errores cuando la URL solicitada no es válida.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleURLErrors(NoResourceFoundException ex) {
        String errorMessage = "La URL solicitada es incorrecta: " + ex.getMessage();
        logger.warn(errorMessage);

        return new ResponseEntity<>(Response.generalError(HttpStatus.NOT_FOUND.value(), errorMessage), HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja excepciones cuando una entidad no es encontrada.
     */
    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Response> handleNotFoundEntityException(NotFoundEntityException ex) {
        logger.error("Entidad no encontrada: {}", ex.getMessage(), ex);

        return new ResponseEntity<>(Response.generalError(HttpStatus.NOT_FOUND.value(), ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    /**
     * Maneja errores al crear una entidad.
     */
    @ExceptionHandler(CreateEntityException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleCreateEntityException(CreateEntityException ex) {
        return handleGenericException(ex, "Error al crear la entidad");
    }

    /**
     * Maneja errores al actualizar una entidad.
     */
    @ExceptionHandler(UpdateEntityException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleUpdateEntityException(UpdateEntityException ex) {
        return handleGenericException(ex, "Error al actualizar la entidad");
    }

    /**
     * Maneja errores al eliminar una entidad.
     */
    @ExceptionHandler(DeleteEntityException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Response> handleDeleteEntityException(DeleteEntityException ex) {
        return handleGenericException(ex, "Error al eliminar la entidad");
    }

    /**
     * Método genérico para manejar excepciones internas del servidor.
     */
    private ResponseEntity<Response> handleGenericException(RuntimeException ex, String defaultMessage) {
        Map<String, String> errors = new HashMap<>();

        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            errors.put("Error", ex.getCause().getMessage());
        }

        logger.error("{}: {}", defaultMessage, ex.getMessage(), ex);

        return new ResponseEntity<>(Response.generalError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
