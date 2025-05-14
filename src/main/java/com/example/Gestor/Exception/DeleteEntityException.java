package com.example.Gestor.Exception;

public class DeleteEntityException extends RuntimeException
{
    public DeleteEntityException(String message, Throwable cause) {
        super(message, cause);
    }

    public <T> DeleteEntityException(T entityClass, Throwable cause)
    {
        super("Error al borrar la entidad: " + entityClass.getClass().getSimpleName() + " - " + cause.getMessage(), cause);
    }
}
