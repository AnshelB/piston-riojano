=

TRUNCATE TABLE detalle_orden, orden_trabajo, vehiculo, cliente, mecanico, repuesto
    RESTART IDENTITY;   


INSERT INTO mecanico (legajo, nombre, apellido, especialidad, valor_hora) VALUES
    (101, 'Luis',  'Herrera', 'MOTOR',        12000.00),
    (102, 'Ana',   'Quiroga', 'ELECTRICIDAD', 11000.00),
    (103, 'Pedro', 'Sosa',    'FRENOS',       10500.00),
    (104, 'Marta', 'Ledesma', 'GENERAL',       9000.00);


INSERT INTO cliente (dni, nombre, apellido, telefono, email) VALUES
    ('30123456', 'Juan',   'Pérez', '3804123456', 'juan.perez@mail.com'),
    ('28456789', 'María',  'Gómez', '3804234567', NULL),                 
    ('35987654', 'Carlos', 'Díaz',  '3804345678', 'carlos.diaz@mail.com');


-- Autos: usan cantidad_puertas y tipo_combustible (cilindrada = NULL)
-- Motos: usan cilindrada (cantidad_puertas y tipo_combustible = NULL)
INSERT INTO vehiculo
    (patente, marca, modelo, anio, dni_cliente, tipo, cantidad_puertas, tipo_combustible, cilindrada)
VALUES
    ('AA123BC', 'Toyota', 'Hilux',  2019, '30123456', 'AUTO', 4,    'DIESEL', NULL),
    ('AD456EF', 'Fiat',   'Cronos', 2021, '28456789', 'AUTO', 4,    'NAFTA',  NULL),
    ('A123BCD', 'Honda',  'XR 150L', 2020, '30123456', 'MOTO', NULL, NULL,     150),
    ('B456CDE', 'Honda',  'CB 500F', 2022, '35987654', 'MOTO', NULL, NULL,     500);

INSERT INTO repuesto (codigo, descripcion, precio_unitario_actual, stock_disponible) VALUES
    ('FIL-ACE-01',   'Filtro de aceite',                15000.00, 19),
    ('ACE-10W40-4L', 'Aceite 10W40 x 4 litros',         52000.00, 14),
    ('FIL-AIR-01',   'Filtro de aire',                  18000.00, 12),
    ('PAS-FRE-DEL',  'Pastillas de freno delanteras',   38000.00,  9),
    ('DIS-FRE-01',   'Disco de freno',                  65000.00,  6),
    ('BAT-12V-65',   'Batería 12V 65Ah',               120000.00,  5),
    ('BUJ-NGK-01',   'Bujía NGK',                        6500.00, 38),
    ('COR-DIS-01',   'Correa de distribución',          45000.00,  8),
    ('LAM-H4-01',    'Lámpara H4',                       4500.00, 30),
    ('CAD-MOT-428',  'Cadena de transmisión moto',      28000.00,  7);

INSERT INTO orden_trabajo
    (fecha_ingreso, descripcion_problema, horas_mano_obra, estado, patente, legajo_mecanico)
VALUES
    ('2026-11-02', 'Service completo: cambio de aceite y filtro', 2.0, 'FINALIZADA',    'AA123BC', 101),
    ('2026-11-09', 'Frenos delanteros gastados',                  1.5, 'EN_REPARACION', 'A123BCD', 103),
    ('2026-11-13', 'No arranca: revisar sistema eléctrico',       1.0, 'PENDIENTE',     'B456CDE', 102);

INSERT INTO detalle_orden (numero_orden, codigo_repuesto, cantidad, precio_aplicado) VALUES
    (1, 'FIL-ACE-01',   1, 15000.00),
    (1, 'ACE-10W40-4L', 1, 52000.00),
    (2, 'PAS-FRE-DEL',  1, 38000.00),
    (3, 'BUJ-NGK-01',   2,  6500.00);

