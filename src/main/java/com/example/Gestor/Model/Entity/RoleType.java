package com.example.Gestor.Model.Entity;

import lombok.Getter;

@Getter
public enum RoleType {
    ADMIN(1),
    USER(2);

    private int value;

    RoleType(int value) {
        this.value = value;
    }
}