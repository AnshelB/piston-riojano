

DROP TABLE IF EXISTS detalle_orden;
DROP TABLE IF EXISTS orden_trabajo;
DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS repuesto;
DROP TABLE IF EXISTS mecanico;
DROP TABLE IF EXISTS cliente;

-- Tablas
CREATE TABLE cliente (
    dni       VARCHAR(10)  PRIMARY KEY,          
    nombre    VARCHAR(50)  NOT NULL,
    apellido  VARCHAR(50)  NOT NULL,
    telefono  VARCHAR(20)  NOT NULL,
    email     VARCHAR(100)                       
);

CREATE TABLE mecanico (
    legajo        INT           PRIMARY KEY,
    nombre        VARCHAR(50)   NOT NULL,
    apellido      VARCHAR(50)   NOT NULL,
    especialidad  VARCHAR(20)   NOT NULL,
    valor_hora    NUMERIC(10,2) NOT NULL,
    CONSTRAINT ck_mecanico_especialidad
        CHECK (especialidad IN ('MOTOR', 'ELECTRICIDAD', 'FRENOS', 'GENERAL')),
    CONSTRAINT ck_mecanico_valor_hora
        CHECK (valor_hora > 0)
);

CREATE TABLE repuesto (
    codigo                 VARCHAR(20)   PRIMARY KEY,
    descripcion            VARCHAR(100)  NOT NULL,
    precio_unitario_actual NUMERIC(12,2) NOT NULL,
    stock_disponible       INT           NOT NULL,
    CONSTRAINT ck_repuesto_precio CHECK (precio_unitario_actual >= 0),
    CONSTRAINT ck_repuesto_stock  CHECK (stock_disponible >= 0)
);


CREATE TABLE vehiculo (
    patente           VARCHAR(10)  PRIMARY KEY,
    marca             VARCHAR(50)  NOT NULL,
    modelo            VARCHAR(50)  NOT NULL,
    anio              INT          NOT NULL,
    dni_cliente       VARCHAR(10)  NOT NULL,
    tipo              VARCHAR(4)   NOT NULL,     -- 'AUTO' o 'MOTO'
    cantidad_puertas  INT,                       -- solo Auto
    tipo_combustible  VARCHAR(10),               -- solo Auto
    cilindrada        INT,                       -- solo Moto
    CONSTRAINT fk_vehiculo_cliente
        FOREIGN KEY (dni_cliente) REFERENCES cliente (dni),
    CONSTRAINT ck_vehiculo_anio
        CHECK (anio BETWEEN 1900 AND 2100),
    CONSTRAINT ck_vehiculo_tipo
        CHECK (tipo IN ('AUTO', 'MOTO')),
    CONSTRAINT ck_vehiculo_combustible
        CHECK (tipo_combustible IN ('NAFTA', 'DIESEL', 'GNC')),
    CONSTRAINT ck_vehiculo_puertas
        CHECK (cantidad_puertas > 0),
    CONSTRAINT ck_vehiculo_cilindrada
        CHECK (cilindrada > 0),
    -- TENER ENCUENTA un auto usa sus campos y no los de moto, y viceversa
    CONSTRAINT ck_vehiculo_coherencia CHECK (
        (tipo = 'AUTO' AND cantidad_puertas IS NOT NULL
                       AND tipo_combustible IS NOT NULL
                       AND cilindrada IS NULL)
        OR
        (tipo = 'MOTO' AND cilindrada IS NOT NULL
                       AND cantidad_puertas IS NULL
                       AND tipo_combustible IS NULL)
    )
);


CREATE TABLE orden_trabajo (
    numero               SERIAL        PRIMARY KEY,   -- correlativo automático
    fecha_ingreso        DATE          NOT NULL DEFAULT CURRENT_DATE,
    descripcion_problema VARCHAR(255)  NOT NULL,
    horas_mano_obra      NUMERIC(6,2)  NOT NULL DEFAULT 0,
    estado               VARCHAR(15)   NOT NULL DEFAULT 'PENDIENTE',
    patente              VARCHAR(10)   NOT NULL,
    legajo_mecanico      INT           NOT NULL,
    CONSTRAINT fk_orden_vehiculo
        FOREIGN KEY (patente) REFERENCES vehiculo (patente),
    CONSTRAINT fk_orden_mecanico
        FOREIGN KEY (legajo_mecanico) REFERENCES mecanico (legajo),
    CONSTRAINT ck_orden_horas
        CHECK (horas_mano_obra >= 0),
    CONSTRAINT ck_orden_estado
        CHECK (estado IN ('PENDIENTE', 'EN_REPARACION', 'FINALIZADA', 'ENTREGADA'))
);


CREATE TABLE detalle_orden (
    numero_orden     INT           NOT NULL,
    codigo_repuesto  VARCHAR(20)   NOT NULL,
    cantidad         INT           NOT NULL,
    precio_aplicado  NUMERIC(12,2) NOT NULL,     -- precio vigente al usarlo
    CONSTRAINT pk_detalle_orden
        PRIMARY KEY (numero_orden, codigo_repuesto),
    CONSTRAINT fk_detalle_orden
        FOREIGN KEY (numero_orden) REFERENCES orden_trabajo (numero)
        ON DELETE CASCADE,                       -- composición: sin orden no hay detalle
    CONSTRAINT fk_detalle_repuesto
        FOREIGN KEY (codigo_repuesto) REFERENCES repuesto (codigo),
    CONSTRAINT ck_detalle_cantidad
        CHECK (cantidad > 0),
    CONSTRAINT ck_detalle_precio
        CHECK (precio_aplicado >= 0)
);
