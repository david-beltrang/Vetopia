# Implementation Plan: Adicionales de la Veterinaria (Fases 0, 1 y 2)

> **Para el agente que ejecuta este plan:** lee primero las secciones "Contexto" y "Reglas generales". Luego ejecuta las fases **en orden** (0 → 1 → 2). No avances de fase hasta cumplir los criterios de aceptación de la anterior. No implementes nada de las fases 3 (correo) ni 4 (reportes), salvo lo que este documento indique expresamente.

---

## 1. Contexto

Proyecto semestral de Desarrollo Web (Pontificia Universidad Javeriana): aplicativo web para una clínica veterinaria de **hospitalización** de perros y gatos.

**Ya implementado (no tocar salvo para integrar):** landing page, CRUD de dueños y mascotas, portal veterinario, portal administrador con CRUD de veterinarios, dashboard con KPIs, carga inicial de medicamentos, tratamientos, historial médico.

**Stack asumido:** Spring Boot + Spring Data JPA (backend) y Angular SPA (frontend), base de datos relacional.

> ⚠️ **Paso 0 obligatorio: inspecciona el repositorio antes de escribir código.** Los nombres reales de clases, paquetes, rutas y propiedades pueden diferir de los usados en este documento (por ejemplo `Mascota`, `Tratamiento`, `Medicamento`, `Veterinario`, `Dueno`). Adapta este plan a lo que encuentres y respeta las convenciones existentes. Si algo contradice este plan de forma importante, **detente y repórtalo** en lugar de improvisar.

Entidades relevantes del negocio:

- `Mascota`: nombre, raza, edad, peso, enfermedad, foto, estado activo/inactivo. Pertenece a un `Dueno`.
- `Medicamento`: nombre, precio de compra, precio de venta, unidades disponibles, unidades vendidas.
- `Tratamiento`: une una `Mascota`, un `Veterinario` y un `Medicamento` en una fecha.
- Una mascota **solo puede recibir tratamiento si está activa**.
- Las mascotas **nunca se borran** al ser dadas de alta; pasan a "inactivas" (solo se eliminan en cascada si se elimina al dueño).

---

## 2. Reglas generales (aplican a todas las fases)

El proyecto se califica con un enfoque *sustractivo*; estas reglas evitan penalizaciones:

1. **Comenta el código** (Javadoc en clases y métodos públicos; comentarios en lógica no obvia). Los comentarios van en español.
2. **Nombres claros** de variables, métodos y clases. Sin abreviaturas crípticas.
3. **Sin código duplicado.** Extrae servicios, funciones o componentes reutilizables.
4. **Validación de formularios y entradas:** Bean Validation (`@NotNull`, `@Positive`, etc.) en el backend y validadores en los formularios de Angular.
5. **Organiza por paquetes coherentes** (ver estructura sugerida en cada fase).
6. **No uses herencia entre entidades.**
7. **Respeta los patrones de diseño ya usados en el proyecto** (revisa cómo están hechos controladores, servicios y repositorios y sigue el mismo estilo).
8. **No expongas entidades JPA directamente en los endpoints nuevos:** usa DTOs.
9. **Dinero:** usa `BigDecimal` en las entidades y DTOs nuevos (nunca `double`/`float`). Escala 2, `RoundingMode.HALF_UP`.
10. **Fechas:** usa `LocalDate` de forma consistente para fechas de ingreso, alta y estadía.
11. **Rendimiento:** evita el problema N+1 (usa `JOIN FETCH` o proyecciones) y no cargues colecciones completas innecesariamente.
12. **Seguridad de secretos:** no subas credenciales al repositorio.
13. **Roles:** si el control de acceso por roles (Feature 13) aún no está implementado, deja los endpoints con la anotación o el mecanismo que el proyecto use y agrega un comentario `// TODO(Feature 13): restringir a ROL_ADMIN` indicando el rol esperado.
14. **Control de versiones:**
    - Parte siempre de `desarrollo` actualizada.
    - Una rama por fase: `feature/fase-0-preparacion`, `feature/alertas-inventario`, `feature/facturacion`.
    - Commits pequeños y descriptivos (por ejemplo `feat(inventario): agrega umbral minimo a Medicamento`).
    - Al terminar cada fase, abre PR/fusiona a `desarrollo`. **No toques `produccion`/`master`.**
15. **Pruebas:** cada fase incluye pruebas automáticas. Las pruebas de backend cargan sus propios datos de prueba y **los eliminan al terminar cada prueba**.

---

## 3. FASE 0: Preparación

**Rama:** `feature/fase-0-preparacion` · **Estimado:** 0.5 día

### Objetivo
Dejar el modelo y las dependencias listos para las fases siguientes, sin cambiar el comportamiento visible.

### Tareas

- [ ] **0.1 Dependencias en `pom.xml`** (revisa antes cuáles existen ya):
  - `spring-boot-starter-validation` (si falta)
  - `com.github.librepdf:openpdf` (PDF; usada en fase 2)
  - `org.apache.poi:poi-ooxml` (Excel; se usará en la fase 4, déjala lista)
  - `spring-boot-starter-mail` y `spring-boot-starter-thymeleaf` (se usarán en la fase 3, déjalas listas)
  
  Usa versiones compatibles con la versión de Spring Boot del proyecto. Verifica que el proyecto compila y arranca después de agregarlas.

- [ ] **0.2 Precio en el momento del tratamiento.** En `Tratamiento` agrega:
  ```java
  /** Precio de venta del medicamento al momento de aplicar el tratamiento (no cambia si luego sube el precio). */
  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precioUnitario;
  ```
  - En el servicio que **crea** tratamientos (AC31), asigna `precioUnitario = medicamento.getPrecioVenta()` en el mismo momento en que se descuentan las unidades.
  - **Datos existentes y datos de prueba:** actualiza el cargador de datos iniciales para que los tratamientos de ejemplo tengan `precioUnitario`. Si la BD ya tiene filas, agrega un script de migración o un `UPDATE` que copie el precio de venta actual del medicamento a los tratamientos existentes. Si se usa `ddl-auto=update`, define un `columnDefinition`/valor por defecto o haz la columna nullable temporalmente y rellena antes de volverla `nullable = false`.
  - Actualiza el DTO de tratamiento/historial para exponer `precioUnitario` solo si ya existe un DTO equivalente.

- [ ] **0.3 Fechas de hospitalización en `Mascota`:**
  ```java
  /** Fecha en que inició la hospitalización actual. Se reinicia al reactivar la mascota. */
  @Column(nullable = false)
  private LocalDate fechaIngreso;

  /** Fecha de alta de la última hospitalización. Null mientras la mascota está hospitalizada. */
  private LocalDate fechaAlta;
  ```
  - Al **crear** una mascota (AC23): `fechaIngreso = LocalDate.now()`, `fechaAlta = null`.
  - En el endpoint de activar/desactivar (AC24): al **activar**, `fechaIngreso = LocalDate.now()` y `fechaAlta = null`. Al desactivar por este endpoint no se factura (ver limitación en la fase 2).
  - Actualiza el cargador de datos de prueba (mascotas con `fechaIngreso` coherentes; las activas con `fechaAlta = null`).
  - Migración para filas existentes: `fechaIngreso` = fecha de hoy o la fecha del primer tratamiento de la mascota, lo que sea más razonable.

- [ ] **0.4 Manejo global de excepciones.** Si no existe, crea un `@RestControllerAdvice` (paquete `exception` o el que use el proyecto) que devuelva un cuerpo JSON uniforme `{ timestamp, status, error, message, path }` para:
  - `EntityNotFoundException` / excepción propia de "no encontrado" → 404
  - Errores de validación (`MethodArgumentNotValidException`) → 400 con detalle por campo
  - Violaciones de regla de negocio (crea `ReglaNegocioException`) → 409 o 422
  - Cualquier otra → 500 sin exponer el stack trace.
  
  Si ya existe, reutilízalo y solo agrega lo que falte.

- [ ] **0.5 Actualizar diagramas.** Anota en el diagrama de clases y en el entidad-relación los campos nuevos de `Tratamiento` y `Mascota` (se completarán en la fase 2).

### Criterios de aceptación (Fase 0)
- El proyecto compila y arranca; los datos iniciales cargan sin errores.
- Crear un tratamiento guarda `precioUnitario`; cambiar luego el precio del medicamento **no** altera tratamientos anteriores.
- Crear una mascota guarda `fechaIngreso`; reactivarla la reinicia.
- Un error de validación y un recurso inexistente devuelven el JSON uniforme.
- Las pruebas existentes siguen pasando.

---

## 4. FASE 1: Alertas de inventario bajo

**Rama:** `feature/alertas-inventario` · **Estimado:** 1 día · **Depende de:** Fase 0

### Objetivo
El administrador ve en el dashboard qué medicamentos están por debajo de su umbral mínimo y cuánto conviene reabastecer.

### Backend

- [ ] **1.1 Entidad `Medicamento`:** agrega
  ```java
  /** Cantidad mínima de unidades disponibles antes de generar una alerta. */
  @Column(nullable = false)
  private Integer umbralMinimo = 10;
  ```
  Con `@Min(0)`. Actualiza el cargador de medicamentos desde archivo (AC30): si el archivo no trae umbral, usa el valor por defecto. Actualiza los datos de prueba para que **al menos 2 medicamentos** queden bajo el umbral.

- [ ] **1.2 Repositorio:** consulta que compare dos columnas de la misma fila:
  ```java
  @Query("SELECT m FROM Medicamento m WHERE m.unidadesDisponibles <= m.umbralMinimo ORDER BY m.unidadesDisponibles ASC")
  List<Medicamento> findMedicamentosBajoUmbral();
  ```
  Y una consulta (o reutiliza la del dashboard) para las **unidades aplicadas en tratamientos de los últimos 30 días** por medicamento. Hazlo en **una sola consulta agrupada**, no una por medicamento.

- [ ] **1.3 DTO `AlertaInventarioDTO`:** `medicamentoId`, `nombre`, `unidadesDisponibles`, `umbralMinimo`, `consumoUltimos30Dias`, `cantidadSugeridaReabastecer`.

- [ ] **1.4 Servicio `InventarioService`** (paquete `inventario` o el equivalente del proyecto):
  - `obtenerAlertas()`: devuelve los DTO de medicamentos bajo umbral.
  - Fórmula de sugerencia: `max(0, consumoUltimos30Dias - unidadesDisponibles)`. Si el resultado es 0 pero el medicamento está bajo umbral, sugiere al menos `umbralMinimo - unidadesDisponibles` para volver al umbral. Documenta la fórmula en un comentario.
  - `actualizarUmbral(id, nuevoUmbral)`: valida `nuevoUmbral >= 0`; lanza excepción "no encontrado" si el medicamento no existe.

- [ ] **1.5 Controlador `InventarioController`:**
  - `GET /api/admin/inventario/alertas` → lista de `AlertaInventarioDTO`.
  - `PUT /api/admin/medicamentos/{id}/umbral` → body `{ "umbralMinimo": 15 }`. Devuelve el medicamento actualizado (o 204).
  - Rol esperado: **ADMIN** (ver regla general 13).

### Frontend (Angular)

- [ ] **1.6 Servicio** `InventarioService` (Angular) con `obtenerAlertas()` y `actualizarUmbral(id, umbral)`. Usa el mecanismo de URL base/entorno que ya exista.
- [ ] **1.7 Componente** `alertas-inventario` (tarjeta reutilizable) dentro del dashboard del administrador:
  - Contador destacado en rojo con la cantidad de medicamentos bajo umbral (verde/neutro si es 0).
  - Tabla: medicamento, disponibles, umbral, consumo 30 días, **sugerido a reabastecer**.
  - Edición del umbral por fila (input numérico con validación `>= 0`) y botón guardar. Muestra mensaje de éxito/error.
  - Estado vacío amable cuando no hay alertas ("Inventario en buen estado").
  - Respeta la paleta de colores y el estilo ya definidos en la aplicación.
- [ ] **1.8 (Opcional) Aviso en el formulario de tratamiento del veterinario:** si el medicamento seleccionado está en o bajo su umbral, muestra un aviso discreto (no bloquea el tratamiento).

### Pruebas (backend)
- [ ] Un medicamento con `unidadesDisponibles <= umbralMinimo` aparece en las alertas; uno por encima **no**.
- [ ] Al aplicar tratamientos hasta cruzar el umbral, el medicamento pasa a aparecer en las alertas.
- [ ] La sugerencia de reabastecimiento se calcula correctamente (caso con consumo alto y caso con consumo 0).
- [ ] `actualizarUmbral` con valor negativo devuelve 400; con id inexistente devuelve 404.

### Criterios de aceptación (Fase 1)
- El dashboard muestra la tarjeta de alertas con datos reales de la BD.
- Al aplicar tratamientos hasta cruzar el umbral, el medicamento aparece en la alerta al refrescar el dashboard (sin intervención manual en la BD).
- Cambiar el umbral desde la UI actualiza la lista.
- Pruebas nuevas pasan y limpian sus datos.

---

## 5. FASE 2: Facturación y cobro de estadía

**Rama:** `feature/facturacion` · **Estimado:** 3 días · **Depende de:** Fases 0 y 1

### Objetivo
Calcular el costo de una hospitalización (estadía + tratamientos), emitir la factura al dar de alta, descargarla en PDF y consultarla desde el portal del veterinario y del cliente.

### Modelo de datos

- [ ] **2.1 `ConfiguracionClinica`** (tabla de una sola fila, `id = 1`):
  - `id` (Long), `tarifaDiaria` (`BigDecimal`, `@PositiveOrZero`).
  - Crea la fila inicial en el cargador de datos (por ejemplo tarifa de 80.000 COP). El servicio `obtenerTarifa()` debe fallar con un error claro si la fila no existe.

- [ ] **2.2 `Factura`:**
  - `id`, `mascota` (`@ManyToOne`, no nulo), `fechaEmision` (`LocalDate`), `diasEstadia` (int), `tarifaDiaria` (copia de la tarifa **al momento de emitir**), `subtotalEstadia`, `subtotalTratamientos`, `total` (todos `BigDecimal`).
  - Relación **1 Mascota → N Facturas** (una por hospitalización).
  - Relación **1 Factura → N Tratamientos**: agrega en `Tratamiento` un campo `@ManyToOne(optional = true) Factura factura`. Un tratamiento con `factura == null` aún no ha sido facturado.
  - **Por qué:** así se evita ambigüedad por fechas si una mascota es dada de alta y reingresa el mismo día. El subtotal de tratamientos = suma de `precioUnitario` de los tratamientos de la mascota **sin factura**.

- [ ] **2.3 Evento de alta (preparación para la fase 3):**
  - Crea `MascotaDadaDeAltaEvent` (record o clase simple) con `mascotaId` y `facturaId`.
  - Publícalo con `ApplicationEventPublisher` al terminar el alta. **No** crees listener ni envíes correo en esta fase; solo el evento.

- [ ] **2.4 Actualiza los diagramas** de clases (con métodos y cardinalidades) y entidad-relación (PK, FK, nulos, únicos) incluyendo `Factura`, `ConfiguracionClinica` y los campos nuevos. Guárdalos en la carpeta de entregables del repositorio.

### Reglas de negocio

1. **Días de estadía** = `ChronoUnit.DAYS.between(fechaIngreso, fechaAlta)`, con **mínimo 1 día**.
2. `subtotalEstadia = diasEstadia × tarifaDiaria`.
3. `subtotalTratamientos` = suma de `precioUnitario` de los tratamientos no facturados de la mascota.
4. `total = subtotalEstadia + subtotalTratamientos`.
5. **Solo se puede dar de alta una mascota activa.** Si ya está inactiva → `ReglaNegocioException` (409/422).
6. El alta es **transaccional** (`@Transactional`): fija `fechaAlta = hoy`, marca la mascota inactiva, crea la factura, asocia los tratamientos a la factura y publica el evento. Si algo falla, no se persiste nada.
7. La tarifa usada queda **copiada** en la factura; cambiar la tarifa después no altera facturas emitidas.
8. **Limitación conocida (documéntala en el README):** desactivar una mascota por el endpoint genérico de AC24 **no** genera factura. El flujo de cobro es el endpoint de alta. Tratamientos no facturados de esa mascota se cobrarán en el siguiente alta.

### Backend

Estructura sugerida: paquete `facturacion` con `model`, `repository`, `service`, `controller`, `dto`, `pdf`.

- [ ] **2.5 `FacturaRepository`** y `ConfiguracionClinicaRepository`. Consultas necesarias: facturas por mascota, y tratamientos no facturados por mascota (con `JOIN FETCH` del medicamento para la factura/PDF).
- [ ] **2.6 `FacturacionService`:**
  - `CuentaDTO calcularCuentaParcial(Long mascotaId)`: calcula la cuenta en tiempo real **sin persistir nada**, con `fechaAlta = hoy` simulada. Devuelve días, tarifa, subtotales, total y el detalle de tratamientos (fecha, medicamento, veterinario, precio).
  - `Factura darDeAltaYFacturar(Long mascotaId)`: aplica las reglas anteriores.
  - `List<FacturaResumenDTO> listarFacturasDeMascota(Long mascotaId)`.
  - `FacturaDTO obtenerFactura(Long id)`.
  - Extrae el cálculo a un método privado reutilizable para que `calcularCuentaParcial` y `darDeAltaYFacturar` **no dupliquen lógica**.
- [ ] **2.7 `ConfiguracionService`:** `obtenerTarifaDiaria()`, `actualizarTarifaDiaria(BigDecimal)` (valida `>= 0`).
- [ ] **2.8 Generación de PDF** con OpenPDF, en la clase `FacturaPdfGenerator` (método `byte[] generar(FacturaDTO factura)`):
  - Encabezado con el nombre y logo de la clínica (usa el logo del proyecto, optimizado; no cargues imágenes pesadas).
  - Datos del dueño y la mascota, fechas de ingreso/alta, días de estadía.
  - Tabla de tratamientos (fecha, medicamento, precio) y resumen de estadía + total.
  - Formato de moneda consistente (COP, separador de miles).
  - Diseña la clase pensando en reutilizarla en la fase 4 (no la acoples a la entidad JPA; recibe el DTO).
- [ ] **2.9 Controladores y endpoints:**

  | Método | Ruta | Rol esperado | Descripción |
  |---|---|---|---|
  | GET | `/api/mascotas/{id}/cuenta` | VETERINARIO | Cuenta parcial en tiempo real |
  | POST | `/api/mascotas/{id}/alta` | VETERINARIO | Da de alta y emite la factura. Devuelve `FacturaDTO` |
  | GET | `/api/mascotas/{id}/facturas` | VETERINARIO, CLIENTE (dueño de esa mascota) | Lista de facturas de la mascota |
  | GET | `/api/facturas/{id}` | VETERINARIO, CLIENTE (dueño) | Detalle |
  | GET | `/api/facturas/{id}/pdf` | VETERINARIO, CLIENTE (dueño) | PDF (`application/pdf`, `Content-Disposition: attachment; filename="factura-{id}.pdf"`) |
  | GET | `/api/admin/configuracion/tarifa` | ADMIN | Consulta la tarifa |
  | PUT | `/api/admin/configuracion/tarifa` | ADMIN | Actualiza la tarifa |

  Un cliente **solo debe poder ver facturas de sus propias mascotas**. Si el control por roles aún no existe, deja la verificación de propiedad implementada en el servicio (lanza excepción si la mascota no pertenece al dueño solicitante) o con TODO claro.

### Frontend (Angular)

- [ ] **2.10 Servicio** `FacturacionService` con métodos para cuenta, alta, facturas, PDF y tarifa. La descarga de PDF usa `responseType: 'blob'` y una función utilitaria reutilizable (`descargarArchivo(blob, nombre)`) en un servicio/utilidad compartida, porque se reutilizará en la fase 4.
- [ ] **2.11 Portal veterinario, detalle de mascota:**
  - Componente `cuenta-mascota`: muestra la cuenta parcial (días acumulados, tarifa, tratamientos, total).
  - Botón **"Dar de alta y facturar"** (solo si la mascota está activa) que abre un **diálogo de confirmación con el resumen** antes de emitir. Tras emitir, muestra la factura y ofrece "Descargar PDF".
  - Lista de facturas anteriores de la mascota con descarga de PDF.
- [ ] **2.12 Portal del cliente, detalle de mascota (AC14):** sección "Facturas" con la lista y botón de descarga de PDF.
- [ ] **2.13 Portal administrador:** pantalla "Configuración" con el formulario de la tarifa diaria (validación `>= 0`, mensaje de éxito/error). Agrega la ruta y la entrada en el menú del admin.
- [ ] **2.14 Rutas, guards y mockups:** agrega las nuevas rutas respetando la estructura de navegación existente y actualiza los mockups/mapa de navegación si el proyecto los mantiene en el repositorio.

### Pruebas (backend)
- [ ] Estadía mínima: alta el mismo día del ingreso → `diasEstadia == 1`.
- [ ] Mascota con 3 días y 4 tratamientos → el total coincide con el cálculo manual (usa valores fijos y verifica el `BigDecimal`).
- [ ] Alta de mascota ya inactiva → falla con la excepción de regla de negocio y no crea factura.
- [ ] Tras el alta: mascota inactiva, `fechaAlta` fijada, tratamientos asociados a la factura y publicado el evento.
- [ ] Cambiar la tarifa y el precio del medicamento después del alta **no** modifica la factura emitida.
- [ ] Reactivar la mascota reinicia `fechaIngreso`; una segunda hospitalización genera una factura distinta que **no** incluye tratamientos ya facturados.
- [ ] El endpoint de PDF devuelve `Content-Type: application/pdf` y un cuerpo no vacío.
- [ ] La cuenta parcial no persiste nada.
- [ ] Todas las pruebas cargan sus propios datos y los eliminan al terminar.

### Criterios de aceptación (Fase 2)
- Una mascota con 3 días de estadía y 4 tratamientos genera una factura cuyo total coincide con el cálculo manual.
- El PDF se descarga, abre correctamente y muestra datos coherentes con la factura en pantalla.
- Cambiar la tarifa o los precios después del alta no altera facturas antiguas.
- El flujo completo funciona desde la UI: tratamiento → ver cuenta → dar de alta y facturar → descargar PDF → el cliente ve su factura.
- Los diagramas actualizados están en el repositorio.

---

## 6. Entregable final del agente

Al terminar las tres fases:

1. Resumen breve (en el PR o en un archivo `CHANGELOG_ADICIONALES.md`) con: qué se implementó por fase, archivos y endpoints nuevos, y decisiones que se apartaron de este plan (con el motivo).
2. Lista de **TODOs pendientes** (por ejemplo los marcados para Feature 13 y la integración con correo de la fase 3).
3. Sección nueva en el `README.md`: descripción de las alertas de inventario y la facturación, cómo configurar la tarifa, y la limitación documentada del endpoint AC24.
4. Si el proyecto usa Swagger/OpenAPI, endpoints anotados.
5. Confirmación de que **todas las pruebas** (existentes y nuevas) pasan y de que el proyecto compila y arranca desde cero con los datos iniciales.

## 7. Qué NO hacer

- No implementar envío de correos ni reportes Excel/PDF generales (fases 3 y 4).
- No modificar `produccion`/`master`.
- No cambiar el comportamiento de los features existentes salvo lo indicado.
- No usar `double`/`float` para dinero.
- No subir credenciales, ni imágenes pesadas innecesarias.
- No introducir herencia entre entidades.
- No dejar código comentado, `System.out.println` ni imports sin usar.
