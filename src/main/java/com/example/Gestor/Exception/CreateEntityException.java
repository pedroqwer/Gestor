package com.example.Gestor.Exception;

public class CreateEntityException extends RuntimeException {

    public CreateEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public <T> CreateEntityException(T entityClass, Throwable cause) {
        super("Error al crear la entidad: " + entityClass.getClass().getSimpleName() + " - " + cause.getMessage(), cause);
    }
}
