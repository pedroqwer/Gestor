CREATE TABLE auditoria
(
    id        BIGINT AUTO_INCREMENT NOT NULL,
    perfil_id BIGINT                NOT NULL,
    accion    VARCHAR(255)          NOT NULL,
    fecha     datetime              NOT NULL,
    detalles  TEXT                  NULL,
    CONSTRAINT pk_auditoria PRIMARY KEY (id)
);

CREATE TABLE cuenta_bancaria
(
    id         BIGINT         NOT NULL,
    usuario_id BIGINT         NOT NULL,
    saldo      DECIMAL(15, 2) NOT NULL,
    moneda     VARCHAR(255)   NOT NULL,
    CONSTRAINT pk_cuenta_bancaria PRIMARY KEY (id)
);

CREATE TABLE movimientos
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    cantidad    DOUBLE                NOT NULL,
    descripcion VARCHAR(255)          NOT NULL,
    fecha       datetime              NOT NULL,
    tipo        SMALLINT              NOT NULL,
    is_perfil   BIGINT                NULL,
    CONSTRAINT pk_movimientos PRIMARY KEY (id)
);

CREATE TABLE perfil
(
    id       BIGINT       NOT NULL,
    nombre   VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    dni      VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL,
    telefono VARCHAR(255) NOT NULL,
    CONSTRAINT pk_perfil PRIMARY KEY (id)
);

CREATE TABLE presupuesto
(
    id                  BIGINT AUTO_INCREMENT NOT NULL,
    perfil              BIGINT                NOT NULL,
    limite_mensual      DOUBLE                NOT NULL,
    descripcion         VARCHAR(255)          NOT NULL,
    presupuesto_del_mes VARCHAR(255)          NULL,
    fecha_del_mes       datetime              NOT NULL,
    CONSTRAINT pk_presupuesto PRIMARY KEY (id)
);

CREATE TABLE `role`
(
    id     INT AUTO_INCREMENT NOT NULL,
    nombre VARCHAR(255)       NOT NULL,
    CONSTRAINT pk_role PRIMARY KEY (id)
);

CREATE TABLE user_role
(
    role_id     INT    NOT NULL,
    user_codigo BIGINT NOT NULL
);

CREATE TABLE usuario
(
    id                 BIGINT AUTO_INCREMENT NOT NULL,
    username           VARCHAR(255)          NOT NULL,
    password           VARCHAR(255)          NOT NULL,
    `creación`         datetime              NOT NULL,
    activo             BIT(1) DEFAULT 1      NOT NULL,
    fecha_ultimo_login datetime              NULL,
    CONSTRAINT pk_usuario PRIMARY KEY (id)
);

ALTER TABLE cuenta_bancaria
    ADD CONSTRAINT uc_cuenta_bancaria_usuario UNIQUE (usuario_id);

ALTER TABLE perfil
    ADD CONSTRAINT uc_perfil_dni UNIQUE (dni);

ALTER TABLE perfil
    ADD CONSTRAINT uc_perfil_email UNIQUE (email);

ALTER TABLE perfil
    ADD CONSTRAINT uc_perfil_telefono UNIQUE (telefono);

ALTER TABLE usuario
    ADD CONSTRAINT uc_usuario_password UNIQUE (password);

ALTER TABLE usuario
    ADD CONSTRAINT uc_usuario_username UNIQUE (username);

ALTER TABLE auditoria
    ADD CONSTRAINT FK_AUDITORIA_ON_PERFIL FOREIGN KEY (perfil_id) REFERENCES perfil (id);

ALTER TABLE cuenta_bancaria
    ADD CONSTRAINT FK_CUENTA_BANCARIA_ON_USUARIO FOREIGN KEY (usuario_id) REFERENCES perfil (id);

ALTER TABLE movimientos
    ADD CONSTRAINT FK_MOVIMIENTOS_ON_IS_PERFIL FOREIGN KEY (is_perfil) REFERENCES perfil (id);

ALTER TABLE perfil
    ADD CONSTRAINT FK_PERFIL_ON_ID FOREIGN KEY (id) REFERENCES usuario (id);

ALTER TABLE presupuesto
    ADD CONSTRAINT FK_PRESUPUESTO_ON_PERFIL FOREIGN KEY (perfil) REFERENCES perfil (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_rol FOREIGN KEY (role_id) REFERENCES `role` (id);

ALTER TABLE user_role
    ADD CONSTRAINT fk_user_role_on_usuario FOREIGN KEY (user_codigo) REFERENCES usuario (id);