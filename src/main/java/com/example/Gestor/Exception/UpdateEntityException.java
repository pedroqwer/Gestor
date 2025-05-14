package com.example.Gestor.Exception;

public class UpdateEntityException extends RuntimeException
{
    public UpdateEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public <T> UpdateEntityException(T entityClass, Throwable cause)
    {
        super("Error al modificar la entidad: " + entityClass.getClass().getSimpleName() + " - " + cause.getMessage(), cause);
    }
}

