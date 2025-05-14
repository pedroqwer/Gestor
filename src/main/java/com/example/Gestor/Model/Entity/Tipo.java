package com.example.Gestor.Model.Entity;

public enum Tipo {
    INGRESO(0),
    GASTO(1);

    private final int value;

    Tipo(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    // Método para obtener un Tipo desde un número
    public static Tipo fromInt(int value) {
        for (Tipo tipo : Tipo.values()) {
            if (tipo.getValue() == value) {
                return tipo;
            }
        }
        throw new IllegalArgumentException("Tipo inválido: " + value);
    }
}
