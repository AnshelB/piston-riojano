# Taller mecánico «Pistón Riojano»

Parcial Final Práctico — Paradigmas y Lenguajes III — UNLaR — Ciclo lectivo 2026

Aplicación de escritorio (Java + Swing + PostgreSQL) para gestionar clientes, vehículos,
mecánicos, repuestos y órdenes de trabajo de un taller mecánico.

**Integrantes:** Olmedo Jimena ELSI926, Bordon Maria de los Angeles ELSI998

## 1. Requisitos

| Herramienta | Versión usada | Observación |
|---|---|---|
| JDK | 26.0.2 | El proyecto compila con `release 17`, por lo que sirve cualquier JDK 17 o superior |
| PostgreSQL | [COMPLETAR: versión, ver nota abajo] | Cualquier versión reciente |
| Maven | El incluido en NetBeans | También funciona un Maven 3.9 o superior |
| NetBeans | [COMPLETAR: versión] | Opcional: se puede compilar desde la línea de comandos |
| pgAdmin 4 | [COMPLETAR: versión] | Opcional: se puede usar `psql` |

> **Importante:** la primera vez que se compila, Maven descarga el driver de PostgreSQL
> (`org.postgresql:postgresql:42.7.4`), por lo que **hace falta conexión a internet** en ese momento.
> Después queda guardado en `~/.m2` y se puede trabajar sin conexión.

> Nota para completar la versión de PostgreSQL: en pgAdmin, abrir la herramienta de consultas
> (Query Tool) sobre cualquier base y ejecutar `SELECT version();`.

## 2. Contenido de la entrega

```
Grupo_Apellido1_Apellido2_Apellido3.zip
├── diseno/
│   ├── diagrama_clases.pdf        Diagrama de clases UML (Parte A)
│   └── modelo_relacional.pdf      Modelo relacional / DER (Parte B)
├── sql/
│   ├── schema.sql                 Creación de las tablas
│   └── datos.sql                  Datos de prueba
├── proyecto/                      Proyecto Maven (NetBeans)
├── README.md                      Este archivo
└── informe.pdf                    Informe de diseño
```

## 3. Crear la base de datos

La base se llama **`taller_piston`** y debe crearse vacía.

### Con pgAdmin 4

1. Clic derecho sobre **Databases** → **Create** → **Database…**, nombre `taller_piston`, guardar.
2. Clic derecho sobre `taller_piston` → **Query Tool**.
3. Abrir `sql/schema.sql` (ícono de carpeta) y ejecutarlo con **F5**. Crea las 6 tablas.
4. Abrir `sql/datos.sql` y ejecutarlo con **F5**. Carga los datos de prueba.

### Con psql (alternativa)

```
psql -U postgres -c "CREATE DATABASE taller_piston;"
psql -U postgres -d taller_piston -f sql/schema.sql
psql -U postgres -d taller_piston -f sql/datos.sql
```

Ambos scripts se pueden ejecutar más de una vez: `schema.sql` empieza con `DROP TABLE IF EXISTS` y
`datos.sql` vacía las tablas antes de cargar los datos. **Atención:** al ejecutar `datos.sql` se pierde
lo que se haya cargado desde la aplicación.

## 4. Configurar la conexión

Los datos de conexión se configuran en **un único lugar**, el archivo de propiedades:

```
proyecto/src/main/resources/db.properties
```

Contiene los parámetros de conexión leídos automáticamente por la clase `Conexion` (`com.mycompany.parcial.dao.Conexion`):

```properties
db.url=jdbc:postgresql://localhost:5432/taller_piston
db.user=postgres
db.password=TU_CLAVE
```

Por defecto:
- `db.url`: `jdbc:postgresql://localhost:5432/taller_piston`
- `db.user`: `postgres`
- `db.password`: contraseña de acceso al servidor local

> **Nota:** La clase `Conexion.java` carga este archivo dinámicamente desde el classpath (`ClassLoader.getResourceAsStream("db.properties")`). Esto permite cambiar las credenciales o el puerto sin necesidad de modificar ni recompilar el código fuente. Se incluye también un archivo de plantilla `db.properties.example`.

## 5. Compilar y ejecutar

### Con NetBeans (recomendado)

1. **File → Open Project…** y elegir la carpeta `proyecto`.
2. Clic derecho sobre el proyecto → **Clean and Build**. Debe terminar en `BUILD SUCCESS`.
3. Clic derecho sobre el proyecto → **Run** (la clase principal es `com.mycompany.parcial.Parcial`).

### Desde la línea de comandos (alternativa)

Dentro de la carpeta `proyecto`:

```
mvn clean compile org.codehaus.mojo:exec-maven-plugin:3.6.3:java -Dexec.mainClass=com.mycompany.parcial.Parcial
```

## 6. Uso de la aplicación

Se abre una ventana principal MDI con tres menús:

| Menú | Opción | Requerimiento | Qué hace |
|---|---|---|---|
| Gestión | Clientes | RF1 | Alta, modificación, baja y listado de clientes. No permite dar de baja a un cliente con vehículos |
| Gestión | Vehículos | RF2 | Alta y listado de los vehículos de un cliente. Según se elija Auto o Moto se habilitan los campos propios de cada tipo |
| Gestión | Repuestos | RF3 | Listado con stock y actualización de precio y stock |
| Órdenes | Nueva orden de trabajo | RF4 | Se elige vehículo y mecánico, se cargan descripción y horas, y se agregan repuestos. Muestra subtotales y total. Al guardar, la orden, su detalle y el descuento de stock se graban en una única transacción |
| Órdenes | Consulta de órdenes | RF5 | Filtra por estado y permite cambiar el estado de la orden seleccionada (PENDIENTE, EN_REPARACION, FINALIZADA, ENTREGADA, en ese orden) |

### Datos de prueba y totales esperados

`datos.sql` carga 3 clientes, 4 vehículos (2 autos y 2 motos), 4 mecánicos, 10 repuestos y 3 órdenes
para verificar el cálculo del total:

| Orden | Vehículo | Total esperado |
|---|---|---|
| 1 | Toyota Hilux diésel, 2 hs con mecánico a $12.000/h | $95.800 (ejemplo del enunciado) |
| 2 | Honda XR 150L (150 cc, coeficiente 0,80) | $50.600 |
| 3 | Honda CB 500F (500 cc, coeficiente 1,00) | $24.000 |

## 7. Estructura del código

```
com.mycompany.parcial          Parcial (main)
com.mycompany.parcial.modelo   Clases del dominio (según el diagrama de clases)
com.mycompany.parcial.dao      Conexion y DAO (único lugar con SQL / JDBC)
com.mycompany.parcial.vista    Ventana principal MDI y formularios (JInternalFrame)
```

Las ventanas no ejecutan SQL: usan los DAO. Todas las consultas usan `PreparedStatement` y
`try-with-resources`.

## 8. Problemas frecuentes

| Mensaje | Causa probable |
|---|---|
| `password authentication failed` | La contraseña configurada en `db.properties` no coincide con la del PostgreSQL local |
| `Connection refused` | El servidor PostgreSQL no está iniciado o el puerto no es el 5432 |
| `database "taller_piston" does not exist` | Falta crear la base (sección 3, paso 1) |
| `relation "cliente" does not exist` | Falta ejecutar `schema.sql` |
| Maven no encuentra el driver | Falta conexión a internet en la primera compilación |

## 9. Limitaciones conocidas

- Cuando una transacción se deshace, el número de orden que reservó la base no se reutiliza: los
  números son únicos y crecientes, pero pueden tener saltos.
- Los importes se manejan con `double` en el modelo, según el diagrama de clases (en la base son `NUMERIC`).
- La lista de clientes de la ventana de vehículos no se refresca sola si se agregan clientes con esa
  ventana abierta: se cierra y se vuelve a abrir.
