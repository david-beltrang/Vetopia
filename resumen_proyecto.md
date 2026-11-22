# Resumen del proyecto — Vetopia

> **Vetopia – Hospital Veterinario del Futuro.** Aplicación web para la gestión de una clínica/hospital veterinario: portal institucional, portal del cliente, portal del veterinario y panel administrativo.
>
> Documento basado en el código fuente actual del repositorio (`src/main/java`, `src/main/resources`, `src/app`, `pom.xml`, `package.json` y documentación de `docs/`). Fecha: noviembre de 2026.

---

## 1. ¿Qué es Vetopia?

Vetopia es una **aplicación web full-stack** que digitaliza la operación de un hospital veterinario. Permite:

- Mostrar la clínica al público (landing con servicios, equipo, funcionamiento y ubicación).
- Autenticar a tres tipos de usuario: **cliente/dueño**, **veterinario** y **administrador**.
- Gestionar **clientes, mascotas, fichas clínicas, tratamientos e inventario de medicamentos**.
- **Agendar citas** para una o varias mascotas en una sola reserva, con asignación automática de veterinario.
- Consultar un **dashboard** con indicadores de negocio (mascotas, tratamientos, ventas y ganancias).

El repositorio contiene **dos aplicaciones que conviven en la raíz**:

| Aplicación | Tecnología | Rol |
|---|---|---|
| **Backend** | Spring Boot 3.5.4, Java 17, Maven | API REST JSON consumida por Angular + portal histórico con Thymeleaf |
| **Frontend** | Angular 19 (standalone), TypeScript 5.7, Tailwind CSS 4 | SPA que renderiza los tres portales y el panel administrativo |

> Nota: el `README.md` está parcialmente desactualizado (menciona Java 21, Vite y puerto 8080). La configuración real del código es **Java 17** (`pom.xml`), **Angular CLI con `@angular/build:application`** (`angular.json`, sin `vite.config.ts`) y backend en **puerto 8090** (`application.properties`, `src/app/config/api.config.ts`).

---

## 2. Implementación y stack tecnológico

### 2.1 Backend

| Concepto | Tecnología / detalle |
|---|---|
| Framework | Spring Boot 3.5.4 (parent Maven) |
| Lenguaje | Java 17, Lombok 1.18.48 (anotaciones `@Data`, `@Builder`, `@Getter`, etc.) |
| Acceso a datos | Spring Data JPA / Hibernate, H2 2.2.224 |
| Base de datos | Perfil por defecto: `jdbc:h2:file:./mydatabase`; perfil `test`: H2 en memoria. Consola H2 en `/h2`. `ddl-auto=create-drop` (el esquema se recrea en cada arranque) |
| Seguridad | Spring Security + JWT (jjwt 0.11.5, firma HS512), `BCryptPasswordEncoder` |
| Vistas legacy | Thymeleaf (`spring-boot-starter-thymeleaf`) |
| Documentación API | Springdoc OpenAPI 2.8.17 (anotaciones `@Operation`, Swagger UI) |
| Integración externa | `RestClient` hacia **OpenRouter** para clasificar el motivo de una cita y sugerir especialidad |
| Utilidades | Apache POI 5.2.3 (lectura del Excel `drogasVetopia.xlsx`), Spring Boot Actuator, MapStruct declarado (sin mappers implementados) |
| Pruebas | JUnit 5, Mockito, MockMvc, `@DataJpaTest`, `@SpringBootTest`, Selenium 4.11 + WebDriverManager (E2E) |

Dependencias reseñables del `pom.xml`: `spring-boot-starter-web`, `-data-jpa` (3.5.14 explícita), `-security`, `-thymeleaf`, `-actuator`, `-test`, `h2`, `jjwt-impl`/`jjwt-jackson` 0.11.5, `poi`/`poi-ooxml` 5.2.3, `springdoc-openapi-starter-webmvc-ui` 2.8.17, `mapstruct` 1.5.5.Final, `selenium-java` 4.11.0.

**Comandos:** `mvn spring-boot:run` (puerto 8090). La clave de OpenRouter se configura solo por entorno (`OPENROUTER_API_KEY`, `OPENROUTER_MODEL`), igual que `JWT_SECRET`.

### 2.2 Frontend

| Concepto | Tecnología / detalle |
|---|---|
| Framework | Angular 19.2 (componentes *standalone*, sin `NgModule`) |
| Lenguaje | TypeScript ~5.7.2 |
| Reactividad | RxJS ~7.8 (`subscribe`, `combineLatest`, `mergeMap`, `map`, `of`) y *signals* (`signal()`) |
| Estilos | Tailwind CSS 4 (`@import "tailwindcss"`, tokens `@theme`), paleta Vetopia;tipografías Hanken Grotesk, Bricolage Grotesque, JetBrains Mono; Material Symbols |
| Build | `@angular/build:application`, salida `dist/vetopia`; `postcss.config.json` con `@tailwindcss/postcss` |
| Pruebas | Karma + Jasmine (`npm test`) |
| Comunicación | `HttpClient` contra `API_URL = http://localhost:8090` |

**Comandos:** `npm install`, `npm start` (equivale a `ng serve`, puerto 4200), `npm run build`, `npm test`.

### 2.3 Estructura de carpetas (resumen)

```
Vetopia/
├── pom.xml                       # Backend Maven
├── package.json / angular.json   # Frontend Angular
├── src/
│   ├── main/java/com/vetopia/
│   │   ├── VetopiaApplication.java
│   │   ├── controller/           # 5 controladores Thymeleaf (portal histórico)
│   │   ├── rest/                 # 9 @RestController (API JSON)
│   │   ├── service/              # interfaces + Impl (lógica de negocio)
│   │   ├── repository/           # 10 interfaces JpaRepository
│   │   ├── entities/             # entidades JPA + enums de dominio
│   │   ├── dto/                  # 18 DTOs de entrada/salida
│   │   ├── security/             # JWT, filtros, UserDetailsService, reglas
│   │   ├── config/               # DataLoader, DataDrogasLoader
│   │   └── exception/            # excepciones y 2 @Advice
│   ├── main/resources/           # application.properties, Excel, plantillas Thymeleaf, estáticos
│   └── app/
│       ├── models/               # interfaces TS y enums (espejo del backend)
│       ├── service/              # 8 servicios HTTP (auth, cita, dashboard, droga, dueño, mascota, tratamiento, veterinario)
│       ├── pages/                # páginas por rol + componentes hijos
│       ├── utils/                # headers/footers y formularios compartidos
│       ├── guards/               # authGuard y rolGuard
│       ├── auth.interceptor.ts   # adjunta el JWT y maneja 401
│       ├── app.routes.ts         # todas las rutas y sus guards
│       └── config/               # API_URL y ubicación de la sede
├── docs/                         # documentación técnica y de autenticación JWT
└── README.md
```

---

## 3. Arquitectura

### 3.1 Flujo general

```
   NAVEGADOR (http://localhost:4200)
        │
        │  Angular SPA: Router → Page Component → Service Angular (HttpClient)
        │  authInterceptor añade  Authorization: Bearer <JWT>
        ▼
   BACKEND SPRING BOOT (http://localhost:8090)
        │
        │  @RestController  →  Service (interfaz + Impl, @Transactional)
        │                   →  Repository (Spring Data JPA)
        │                   →  Entity (@Entity)  →  Hibernate  →  H2
        │
        │  Respuesta JSON (entidad o DTO) + ErrorResponse uniforme si falla
        ▼
   ANGULAR desempaqueta el JSON y actualiza la vista
```

**Idea central:** Angular nunca accede a la base de datos. Toda la lógica de negocio (validaciones, reglas de agenda, descuento de stock, cálculo de KPIs) vive en la capa `service` del backend. El frontend valida formularios para UX y renderiza.

### 3.2 Capas del backend

- **Controladores REST (`rest/`)**: reciben HTTP, extraen `@PathVariable`, `@RequestParam` y `@RequestBody`, llaman al servicio y devuelven JSON. No contienen reglas de negocio. Llevan `@CrossOrigin(origins = "http://localhost:4200")` y `@Operation`.
- **Controladores MVC (`controller/`)**: sirven el portal Thymeleaf histórico (`landing`, `login`, portales de cliente y veterinario). El identificador de usuario viaja por query params (`veterinarioId`, `duenoId`); son el legado previo a la SPA.
- **Servicios (`service/…`)**: interfaces + implementaciones con `@Service`. Aquí están las reglas de negocio, la orquestación entre repositorios y la transaccionalidad (`@Transactional`). Casos destacados: `CitaServiceImpl` (inyección por constructor, 5 repositorios + clasificador), `TratamientoServiceImpl` (coordina drogas, veterinarianos y mascotas), `DashboardServiceImpl`, `MascotaServiceImpl`, `DuenoServiceImpl`, `VeterinarioServiceImpl`, `LoginServiceImpl`, `ImagenServiceImpl`.
- **Repositorios (`repository/`)**: 10 interfaces `JpaRepository` con consultas derivadas (`findByDueno_Id`, `findByMascotaId`, `findByCorreoIgnoreCase…`, `countByEstado`) y algunas consultas `@Query` (solapamientos de citas, especialidades activas, sumas y agrupaciones para el dashboard).
- **Entidades (`entities/`)**: mapeo JPA a tablas H2; relaciones `@ManyToOne`, `@OneToMany`, `@OneToOne`, `@ManyToMany`.
- **DTOs (`dto/`)**: objetos de transporte (crear cita, detalle de cita, historial de tratamiento, KPIs, login, errores). Evitan exponer entidades crudas y resuelven nombres en el servidor.
- **Seguridad (`security/`)**: `SecurityConfig`, `JWTGenerator`, `JWTAuthenticationFilter`, `JwtAuthEntryPoint`, `CustomUserDetailService`, `CorsConfig`.
- **Excepciones (`exception/`)**: `RecursoNoEncontradoException` → 404, `ParametroInvalidoException` → 400, `CredencialesInvalidasException` → 401. `RestExceptionHandler` devuelve un `ErrorResponse` uniforme para la API; `GlobalExceptionHandler` devuelve la vista `error` para Thymeleaf. No se usa Bean Validation: las validaciones son manuales en los servicios.

### 3.3 Capas del frontend

- **Bootstrap**: `main.ts` → `bootstrapApplication(AppComponent, appConfig)`; `AppComponent` solo contiene `<router-outlet>`.
- **Configuración global** (`app.config.ts`): `provideRouter(routes)` y `provideHttpClient(withInterceptors([authInterceptor]))`.
- **Rutas** (`app.routes.ts`): rutas públicas (`/`, `/login`, `/error`, wildcard) y rutas protegidas con `canActivate: [authGuard, rolGuard([...])]` para los tres roles.
- **Guards**: `authGuard` exige token en `localStorage`; `rolGuard(rolesPermitidos)` compara el rol guardado y redirige a `/error` si no coincide.
- **Interceptor**: añade `Authorization: Bearer <token>` a cada petición; si recibe 401 (excepto en `/login` y `/auth/validate`), limpia `token`, `rol` y `userId` y navega a `/login`.
- **Modelos (`models/`)**: interfaces TypeScript que espejean el JSON del backend (`Mascota`, `Dueno`, `Veterinario`, `Tratamiento`, `Droga`, `Cita`, DTOs) y enums como *union types* (`Especie`, `Sexo`, `Estado`, `EstadoCita`).
- **Servicios HTTP (`service/`)**: clases `@Injectable({ providedIn: 'root' })` que agrupan las peticiones y devuelven `Observable<T>`.
- **Páginas (`pages/`)**: componentes con ruta que hacen HTTP y navegan. **Componentes hijos (`components/`, `utils/`)**: presentacionales, reciben `input()` y emiten `output()`; no llaman a servicios.
- **Técnicas usadas**: control de flujo `@if`/`@for` (sin `*ngIf`/`*ngFor`), formularios reactivos (`FormGroup`, `Validators`, `getRawValue()`), `signal()` para estado local, `combineLatest` para peticiones paralelas, `mergeMap` para peticiones dependientes, `of(undefined)` como relleno condicional y `history.state` para pasar el resultado de un tratamiento entre pantallas sin ponerlo en la URL.

### 3.4 Autenticación y roles

Existen **dos sistemas de login paralelos**:

1. `POST /auth/login` → `LoginServiceImpl` busca en veterinarios → administradores → dueños (comparación de contraseña en texto plano) y devuelve `{ id, rol, correo }`, donde `rol` es `VETERINARIO`, `ADMINISTRADOR` o `CLIENTE`. Es un login **informativo**: identifica al usuario y su rol para el frontend.
2. `POST /veterinario/login`, `POST /dueno/login` (y `POST /administrador/login`) usan `AuthenticationManager` + BCrypt y devuelven un **JWT** como texto plano.

`AuthService.login()` encadena ambos: primero identifica el rol y luego pide el JWT al endpoint correspondiente (administradores entran por `/veterinario/login`). Guarda `token`, `rol` y `userId` en `localStorage`. El token contiene `sub`, `iat` y `exp` (firma HS512, validez ~11 min 40 s) **sin el rol**; las autoridades se recargan desde la tabla `roles` en cada petición mediante `CustomUserDetailService`.

Reglas de `SecurityConfig` (autoridades de BD: **`ADMIN`, `DUENO`, `VETERINARIO`**; el frontend usa `ADMINISTRADOR` y `CLIENTE`):

| Recurso | Regla |
|---|---|
| `/h2/**`, `/dueno/login`, `/veterinario/login`, `/auth/login` | público |
| `/dueno/details`, `/veterinario/details`, `/auth/validate` | `authenticated()` (JWT válido) |
| `POST /dueno`, `PUT/DELETE /dueno/**`, `POST /mascota`, `PUT/DELETE /mascota/**` | autenticado + authority `VETERINARIO` |
| `POST /veterinario` | autenticado + authority `ADMIN` |
| Resto de `/mascota/**` | `authenticated()` |
| Cualquier otra ruta (incluidas `/cita/**`, `/tratamiento/**`, `/dashboard/**`, `/droga/**`, `/administrador/**`) | `permitAll()` |

Sin token en una ruta protegida → **401** (`JwtAuthEntryPoint`); con token de otro rol → **403**. Los guards de Angular protegen la navegación, pero **la autorización real de escritura está en el backend solo para dueños, mascotas y alta de veterinarios**.

### 3.5 Persistencia y datos de prueba

- `DataLoader` (`@Profile("!test")`): siembra con semilla fija `Random(42L)` **3 roles**, **50 dueños**, **5 veterinarios**, **3 administradores**, drogas leídas del Excel `drogasVetopia.xlsx`, **100 mascotas**, **80 tratamientos** y hasta **8 citas de demostración**. Crea usuarios con contraseñas BCrypt para login JWT.
- `DataLoaderTest` (`@Profile("test")`): misma cantidad de datos, en H2 en memoria, sin usuarios ni citas demo.
- Credenciales sembradas: `veterinario@vetopia.com / vet123`, `admin@vetopia.com / admin123`, clientes `cliente<cedula>@correo.com / dueno123`.
- Con `ddl-auto=create-drop`, los datos se regeneran en cada arranque.

> Precaución: `application.properties` tiene los separadores de perfil **comentados** (`#---`) y claves duplicadas (`on-profile`, `spring.datasource.url`); conviene normalizarlo antes de dar por cierta la precedencia efectiva entre H2 archivo y H2 memoria.

### 3.6 Integración con OpenRouter

`ClasificadorMotivoCita` envía el motivo y la lista de especialidades activas a `https://openrouter.ai/api/v1/chat/completions` (timeouts 4 s de conexión y 12 s de lectura, `temperature=0`). Normaliza la respuesta y solo acepta una especialidad de la lista; si no hay clave, hay error/timeout o el texto no coincide, devuelve `Optional.empty()` y la interfaz pide **selección manual**. La credencial nunca se envía al frontend ni se guarda en el repositorio.

---

## 4. Funcionamiento

### 4.1 Arranque y navegación

1. Arranca el backend en `http://localhost:8090` (consola H2 en `/h2`, Swagger UI disponible vía springdoc).
2. Arranca el frontend con `npm start` en `http://localhost:4200`.
3. La landing (`/`) ofrece acceso a `/login`. Si ya hay un token válido, `LandingPageComponent` lo valida con `/auth/validate` y redirige según el rol:
   - `VETERINARIO` → `/veterinario/mascotas?veterinarioId=<id>`
   - `CLIENTE` → `/cliente/mascotas?duenoId=<id>`
   - `ADMINISTRADOR` → `/admin/dashboard?adminId=<id>`

### 4.2 Flujo de login

```
Formulario de login → POST /auth/login → {id, rol, correo}
        → POST /dueno/login o /veterinario/login → JWT
        → localStorage {token, rol, userId}
        → redirección por rol (query params con el id)
        → cada petición lleva Authorization: Bearer <JWT>
        → logout() limpia localStorage y vuelve a /login
```

El interceptor detecta 401 y fuerza el cierre de sesión. Los tres headers (cliente, veterinario, administrador) implementan logout.

### 4.3 Agenda de citas

**Reglas de negocio (`CitaServiceImpl`):**

- Zona horaria `America/Bogotá`; jornada **lunes a sábado, 09:00–13:00 y 14:00–18:00**; domingo no permitido.
- **30 minutos por mascota**, máximo **5 mascotas por cita**; inicio alineado a media hora; fecha futura.
- Especialidad debe corresponder a veterinarios **activos**.
- Un horario es disponible si al menos un veterinario de la especialidad no tiene solapamiento con citas `CONFIRMADA`.
- Al crear, se elige el veterinario con **menor carga del día** (menos citas confirmadas), se aplica **bloqueo pesimista** para evitar reservas concurrentes y se revalidan estado/especialidad/solapamiento.
- La cita se guarda `CONFIRMADA` con la especialidad real del veterinario y una fila `CitaMascota` por mascota.
- El cliente puede cancelar solo con **≥24 h de antelación**; el administrador puede cancelar sin esa regla.
- El veterinario solo puede marcar `COMPLETADA` o `INASISTENCIA` en sus propias citas confirmadas.
- Las consultas de agenda admiten rangos de hasta 32 días.

**Flujo del cliente:** elegir mascotas → escribir motivo (mínimo 8 caracteres) → clasificación opcional con OpenRouter o elegir especialidad manual → seleccionar fecha → consultar `GET /cita/disponibilidad` → confirmar con `POST /cita` → ver historial y cancelar si aplica.

**Flujo del veterinario / administrador:** agenda semanal de lunes a sábado, navegación entre semanas, acciones según modo (completar/no asistió o cancelar) sobre la tabla `CitaMascota` y `Cita`.

### 4.4 Tratamientos e inventario

Desde la ficha clínica se accede a **Asignar tratamiento** (bloqueado si la mascota está inactiva). El formulario muestra el stock de cada droga y deshabilita las agotadas; incluye validador de respaldo y fecha por defecto = hoy.

`POST /tratamiento/asignar?veterinarioId=` ejecuta, **en una sola transacción**: valida mascota activa y droga con stock, resuelve el veterinario, guarda el tratamiento con `completado=false`, descuenta 1 unidad (`unidadesDisponibles--`, `unidadesVendidas++`) y devuelve `ResultadoTratamientoDTO`. La pantalla de confirmación muestra medicamento, unidades descontadas y stock restante (datos pasados por `history.state`). El listado «Tratamientos» permite alternar **Completado/Pendiente** con `PUT /tratamiento/{id}/estado`.

### 4.5 Dashboard

`/admin/dashboard` compone: KPIs (`/dashboard/kpis`: total mascotas, mascotas en tratamiento, tratamientos últimos 30 días, ventas y ganancias), total de tratamientos (`/tratamiento/total-aplicados`), veterinarios activos/inactivos (`/veterinario/activos/count`, `/inactivos/count`), ranking **Top 3** de tratamientos (`/dashboard/top-tratamientos`) y tabla **Top 6** de medicamentos del último mes (`/dashboard/medicamentos-ultimo-mes`). Los importes se formatean en COP (`Intl.NumberFormat('es-CO')`). Los componentes recargan al entrar y en cada `NavigationEnd`.

### 4.6 Operaciones CRUD

- **Cliente:** alta (crea usuario con rol `DUENO`), listado con filtro local, detalle con mascotas, edición y baja en cascada (mascotas y tratamientos).
- **Mascota:** alta (fija estado `Activo`, valida color y fecha de ingreso, guarda imagen), edición que conserva dueño/estado/imagen si no se envían, cambio de estado, baja en cascada. El formulario Angular usa `FileReader` → base64 en el campo `imagen`; el controlador Thymeleaf acepta `MultipartFile`.
- **Veterinario:** alta (crea usuario con rol `VETERINARIO`, restringida a `ADMIN`), listado y filtros, detalle con número de atenciones, cambio de estado y baja que **desvincula sus tratamientos** en lugar de borrarlos.
- **Droga:** CRUD disponible en la API; el flujo de negocio consume `GET /droga/all` y el descuento vía `/tratamiento/asignar`.

---

## 5. Entidades

### 5.1 Entidades JPA (backend)

| Entidad / tabla | Campos principales | Relaciones y reglas |
|---|---|---|
| **`Administrador`** (`administradores`) | `id`, `cedula`, `correo`, `contrasena`, `nombre`, `user` | `@OneToOne(cascade=ALL)` con `UserEntity`. Login por correo/contraseña. |
| **`Cita`** (`citas`) | `id`, `dueno`, `veterinario`, `fechaHoraInicio`, `fechaHoraFin`, `motivo`, `especialidad`, `estado` (`EstadoCita`), `creadaEn` | `@ManyToOne` obligatorias a dueño y veterinario. Estado persistido como texto. |
| **`CitaMascota`** (`cita_mascotas`) | `id`, `cita`, `mascota` | Tabla de asociación para citas grupales: una cita puede incluir varias mascotas. |
| **`Droga`** (`droga`) | `id`, `nombre`, `precioCompra`, `precioVenta`, `unidadesDisponibles`, `unidadesVendidas` | `@OneToMany` con `Tratamiento` (`@JsonIgnore`). El stock se descrementa al asignar tratamientos. |
| **`Dueno`** (`dueno`) | `id`, `cedula` (unique), `nombre`, `correo` (unique), `contrasena`, `celular` (unique), `user`, `mascotas` | `@OneToOne` con `UserEntity` (rol `DUENO`); `@OneToMany(cascade=ALL)` con `Mascota` → borrado en cascada. |
| **`Veterinario`** (`veterinarios`) | `id`, `cedula`, `nombre`, `correo`, `contrasena`, `especialidad`, `numeroAtenciones`, `estado`, `urlFoto`, `user`, `tratamientos` | `@OneToOne` con `UserEntity` (rol `VETERINARIO`); `@OneToMany` tratamientos sin cascade (se desvinculan al eliminar). |
| **`Mascota`** (`mascotas`) | `id`, `nombre`, `especie`, `raza`, `edad` (Double), `sexo`, `imagen`, `pesoKg`, `color`, `fechaIngreso`, `dueno`, `estado`, `tratamientos` | `@ManyToOne` a `Dueno`; `@OneToMany(cascade=ALL)` a `Tratamiento`. `getDuenoId()` expone solo el id en JSON; `tieneImagen()` y `obtenerEdadDescripcion()` son helpers de vista. |
| **`Tratamiento`** (`tratamientos`) | `id`, `fecha`, `titulo`, `descripcion`, `completado`, `mascota`, `veterinario`, `droga` | FK a mascota y droga con `@OnDelete(CASCADE)`; veterinario nullable para conservar historial tras borrarlo. |
| **`UserEntity`** (`users`) | `id`, `username`, `password` (BCrypt), `roles` | `@ManyToMany(EAGER)` con `Role` mediante `user_roles`. Base del login JWT. |
| **`Role`** (`roles`) | `id`, `name` | Valores sembrados: `ADMIN`, `DUENO`, `VETERINARIO`. |
| **`EstadoCita`** (enum) | `CONFIRMADA`, `CANCELADA`, `COMPLETADA`, `INASISTENCIA` | Persistido con `EnumType.STRING`. |

**Enums de dominio** (`entities/mascota/dominio_attr/`): `DominioEspecie` (`PERRO/GATO/OTRO`), `DominioSexo` (`MACHO/HEMBRA`) y `DominioEstado` (`ACTIVO/INACTIVO`, reutilizado para mascotas y veterinarios). Usan `@JsonValue` para serializar valores legibles («Perro», «Macho», «Activo») y `@JsonCreator` (`fromValor`) para convertir el texto entrante; valores desconocidos producen `ParametroInvalidoException` (400). En BD se guarda el nombre del enum.

### 5.2 DTOs principales

| DTO | Uso |
|---|---|
| `CrearCitaDTO` (record) | Entrada: `duenoId`, `mascotaIds`, `motivo`, `especialidad`, `fechaHoraInicio`. |
| `CitaDTO` / `MascotaCitaDTO` | Salida de cita con nombres de dueño/veterinario, mascotas, estados y especialidad. |
| `HorarioDisponibleDTO` | Horarios libres (`fechaHoraInicio`, `fechaHoraFin`). |
| `ClasificacionMotivoCitaDTO` / `SolicitudClasificacionMotivoDTO` | Clasificación de motivo con OpenRouter y bandera `requiereSeleccionManual`. |
| `HistorialTratamientoDTO` | `tratamiento` + `drogaNombre` + `clienteNombre` resueltos en servidor. |
| `ResultadoTratamientoDTO` | Confirmación: tratamiento, mascota, droga, unidades descontadas y stock restante. |
| `DashboardKpisDTO`, `DashboardTopTratamientoDTO`, `DashboardMedicamentoDTO` | Indicadores y rankings del panel administrativo. |
| `DuenoDTO`, `VeterinarioDTO`, `AdministradorDTO` | Salida de usuarios (el DTO de veterinario omite contraseña y relaciones). |
| `LoginRequestDTO`, `ResultadoLogin`, `ErrorResponse` | Login y respuestas de error uniformes. |

### 5.3 Modelos del frontend

`src/app/models/` contiene las interfaces TypeScript equivalentes (`Mascota`, `Dueno`, `Veterinario`, `Administrador`, `Tratamiento`, `Droga`, `Cita`, `MascotaCita`, `CrearCita`, `HorarioDisponible`, `TratamientoDetalle`, `ResultadoTratamientoDTO`, `ResultadoLogin`, modelos de dashboard) y los enums `Especie`, `Sexo`, `Estado` y `EstadoCita`, con sus arrays constantes (`ESPECIES`, `SEXOS`, `ESTADOS`) para los `<select>`.

---

## 6. Funcionalidades actuales por rol

> Las rutas son las de la SPA Angular; entre paréntesis se indican los endpoints que las soportan. El rol se controla con `authGuard` + `rolGuard([...])` en el frontend y, para las escrituras sensibles, con autoridades JWT en el backend.

### 6.1 Público (sin autenticación)

| Funcionalidad | Dónde |
|---|---|
| Ver la landing institucional: hero, servicios, equipo (carrusel), cómo funciona, ubicación con mapa embebido y enlaces de contacto | `/` → `LandingPageComponent` + `HeaderComponent`, `HeroComponent`, `ServicesComponent`, `TeamComponent`, `HowItWorksComponent`, `LocationComponent`, `FooterComponent` |
| Iniciar sesión con correo y contrasena; mensaje de error si las credenciales son incorrectas | `/login` → `POST /auth/login` + `/dueno/login` o `/veterinario/login` |
| Redirección automática al portal correspondiente si ya existe una sesión válida | `LandingPageComponent.ngOnInit()` → `GET /auth/validate` |
| Página de error con opciones «volver al inicio» y «regresar» | `/error` y ruta wildcard `**` |
| Portales históricos con Thymeleaf: landing y formulario de login | `GET /`, `GET /login`, `POST /login` |

### 6.2 Cliente / Dueño (`rolGuard(['CLIENTE'])`)

| Funcionalidad | Ruta UI / API |
|---|---|
| Listar sus mascotas (foto, nombre, especie, raza, edad, sexo, estado) y acceder a su detalle | `/cliente/mascotas` → `GET /mascota/dueno/{duenoId}`; legacy `GET /cliente/mascotas?duenoId=` |
| Consultar el detalle de una mascota: datos, foto y **historial clínico** de tratamientos | `/cliente/mascotas/detalle?id=&duenoId=` → `GET /mascota/{id}` + `GET /tratamiento/mascota/{id}/historial` |
| Reservar una cita para **hasta 5 mascotas** en la misma reserva | `/cliente/citas` → `GET /mascota/dueno/{id}`, `GET /cita/especialidades`, `POST /cita` |
| Escribir un motivo y solicitar sugerencia automática de especialidad (IA), o elegirla manualmente | `POST /cita/clasificar-motivo` (mínimo 8 caracteres; fallback manual si no hay clave de OpenRouter) |
| Elegir fecha y consultar horarios disponibles (jornada 9–13 y 14–18, 30 min por mascota) | `GET /cita/disponibilidad?fecha=&cantidadMascotas=&especialidad=` |
| Consultar su historial de citas, con estado y mascotas incluidas | `GET /cita/dueno/{duenoId}` |
| Cancelar una cita propia confirmada (con al menos 24 h de antelación) | `PUT /cita/{id}/cancelar?duenoId=` (confirmación previa en la UI) |
| Ver la ubicación de la sede en un mapa embebido y enlace «cómo llegar» | Componente `LocationComponent` en citas y landing (`config/ubicacion-vetopia.ts`) |
| Cerrar sesión | `HeaderClienteComponent.cerrarSesion()` → `AuthService.logout()` |

### 6.3 Veterinario (`rolGuard(['VETERINARIO'])`)

| Funcionalidad | Ruta UI / API |
|---|---|
| Listar todas las mascotas, con filtro por nombre y filtro «solo atendidas por mí» | `/veterinario/mascotas` → `GET /mascota/all`, `GET /dueno/all`, `GET /veterinario/{id}`, `GET /tratamiento/veterinario/{id}/historial`; legacy `GET /veterinario/mascotas?veterinarioId=` |
| Ver la **ficha clínica** de una mascota: datos, propietario, estado y tratamientos | `/veterinario/mascotas/ficha/:id` → `GET /mascota/{id}`, `GET /dueno/{id}`, `GET /tratamiento/mascota/{id}/historial` |
| Activar/desactivar una mascota | `PUT /mascota/{id}/estado?estado=Activo\|Inactivo` (desde `EstadoPacienteComponent`); legacy `POST /ficha/estado` |
| Registrar y editar mascotas con foto, desde el listado o desde el detalle del cliente | `/veterinario/mascotas/registrar-mascota/:duenoId`, `/veterinario/mascotas/editar/:id` → `POST /mascota?duenoId=`, `PUT /mascota/{id}`; opción «guardar y limpiar» para cargar varias |
| Registrar, listar, filtrar, ver, editar y eliminar clientes (baja en cascada) | `/veterinario/clientes`, `/clientes/detalle/:id`, `/clientes/editar/:id`, `/mascotas/registrar-cliente` → `GET/POST/PUT/DELETE /dueno` |
| Asignar tratamientos con control de stock y ver confirmación detallada | `/veterinario/mascotas/asignar-tratamiento/:id` → `GET /droga/all`, `POST /tratamiento/asignar?veterinarioId=`; confirmación en `/mascotas/tratamiento-confirmacion/:mascotaId` |
| Consultar el historial de tratamientos de sus pacientes y alternar **Completado/Pendiente** | `/veterinario/mascotas/tratamientos` → `GET /tratamiento/veterinario/{id}/historial`, `PUT /tratamiento/{id}/estado?completado=` |
| Consultar su **agenda semanal** (lunes a sábado), navegar entre semanas y marcar citas como **Completada** o **No asistió** | `/veterinario/citas` → `GET /cita/veterinario/{id}?desde=&hasta=`, `PUT /cita/{id}/estado?veterinarioId=&estado=` |
| Acceder al perfil del dueño desde la ficha clínica | `/veterinario/clientes/detalle/:id` |
| Cerrar sesión | `HeaderVeterinarioComponent.navigateToLogin()` |

### 6.4 Administrador (`rolGuard(['ADMINISTRADOR'])`)

| Funcionalidad | Ruta UI / API |
|---|---|
| Ver el **dashboard**: total de mascotas, mascotas en tratamiento, tratamientos de los últimos 30 días, total de tratamientos, veterinarios activos/inactivos, ventas y ganancias (COP) | `/admin/dashboard` → `GET /dashboard/kpis`, `GET /tratamiento/total-aplicados`, `GET /veterinario/activos/count`, `GET /veterinario/inactivos/count` |
| Ranking **Top 3 Tratamientos** y tabla **Top 6 Medicamentos del último mes** con porcentajes | `/admin/dashboard` → `GET /dashboard/top-tratamientos`, `GET /dashboard/medicamentos-ultimo-mes` |
| Ver la **agenda general** de citas, filtrar por veterinario y estado, y ver contadores de confirmadas/canceladas/inasistencias | `/admin/citas` → `GET /cita/administracion?desde=&hasta=`, `GET /veterinario/all` |
| Cancelar cualquier cita confirmada (sin la regla de 24 h del cliente) | `PUT /cita/{id}/cancelar-administrador` |
| Listar y filtrar veterinarios por nombre o cédula | `/admin/veterinarios` → `GET /veterinario/all` |
| Registrar veterinarios (con foto; crea usuario con rol `VETERINARIO`) | `/admin/registrar-veterinario` → `POST /veterinario` (exige autoridad `ADMIN`) |
| Ver el detalle de un veterinario con su número de atenciones, cambiar su estado y eliminarlo | `/admin/detalles-veterinario/:id` → `GET /veterinario/{id}`, `GET /veterinario/{id}/atenciones/count`, `PUT /veterinario/{id}/estado`, `DELETE /veterinario/{id}` |
| Cerrar sesión | `HeaderAdminComponent.navigateToLogin()` |

### 6.5 Inventario de endpoints REST (referencia)

| Recurso | Endpoints |
|---|---|
| `/auth` | `POST /login`, `GET /validate` |
| `/dueno` | `GET /all`, `GET /{id}`, `GET /{id}/mascotas`, `GET /details`, `POST /login`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/veterinario` | `GET /all`, `GET /{id}`, `GET /details`, `GET /activos/count`, `GET /inactivos/count`, `GET /{id}/atenciones/count`, `POST /login`, `POST`, `PUT /{id}`, `PUT /{id}/estado`, `DELETE /{id}` |
| `/mascota` | `GET /all`, `GET /{id}`, `GET /dueno/{duenoId}`, `GET /buscar?nombre=`, `POST ?duenoId=`, `PUT /{id}`, `PUT /{id}/estado`, `DELETE /{id}` |
| `/tratamiento` | `GET /all`, `GET /{id}`, `GET /mascota/{id}`, `GET /mascota/{id}/historial`, `GET /veterinario/{id}`, `GET /veterinario/{id}/historial`, `GET /total-aplicados`, `POST`, `POST /asignar?veterinarioId=`, `PUT /{id}`, `PUT /{id}/estado`, `DELETE /{id}` |
| `/droga` | `GET /all`, `GET /{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/administrador` | `GET /all`, `GET /{id}`, `POST /login`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `/cita` | `POST /clasificar-motivo`, `GET /especialidades`, `GET /disponibilidad`, `POST`, `GET /dueno/{id}`, `GET /veterinario/{id}`, `GET /administracion`, `PUT /{id}/cancelar`, `PUT /{id}/cancelar-administrador`, `PUT /{id}/estado` |
| `/dashboard` | `GET /kpis`, `GET /top-tratamientos`, `GET /medicamentos-ultimo-mes` |

**Total: 65 endpoints REST** y **23 rutas Thymeleaf** legacy (`/`, `/login`, `/cliente/mascotas`, `/veterinario/mascotas`, `/veterinario/clientes`, `/admin`, etc.). Las rutas legacy mantienen la misma lógica de negocio a través de los mismos servicios.

---

## 7. Pruebas

| Tipo | Archivos | Cobertura |
|---|---|---|
| Unitarias (Mockito) | `CitaServiceMockTest`, `TratamientoServiceMockTest` | Asignación de veterinario con menor carga, duración por mascota, exclusión de domingos/almuerzo, fallback de clasificación, rechazo de mascota inactiva |
| Integración (`@SpringBootTest`, perfil `test`) | `CitaServiceIntegrationTest`, `TratamientoServiceIntegrationTest` | Creación de citas y rechazo de solapamientos; asignación de tratamiento con descuento de stock; alternancia de estado |
| Repositorio (`@DataJpaTest`) | `TratamientoRepositoryTest`, `DuenoRepositoryTest` | Consultas de ventas/ganancias y agrupaciones; CRUD de dueños y borrado en cascada |
| Controlador (MockMvc) | `VeterinarioControllerTest` | Listar, buscar, alta, actualización, baja y conteo de activos |
| E2E (Selenium) | `RegistrarClienteTest`, `NuevoMedicamentoTest`, `CitasClienteE2ETest` | Flujo completo login → alta de cliente y mascota → portal del cliente; asignación de medicamento con verificación de KPIs; reserva de cita con fallback manual y enlace a Maps |

---

## 8. Estado actual y consideraciones

- **Dos vocabularios de roles**: BD/Spring Security usa `ADMIN`, `DUENO`, `VETERINARIO`; `LoginService` y el frontend usan `ADMINISTRADOR`, `CLIENTE`, `VETERINARIO`. El token JWT no incluye el rol.
- **Doble mecanismo de login**: `/auth/login` identifica el rol con contraseña en texto plano, mientras que los `/login` por entidad emiten JWT con BCrypt.
- **Autorización parcial**: los guards protegen la UI, pero en el backend solo están restringidas las escrituras de dueños/mascotas y el alta de veterinarios; citas, tratamientos, drogas, dashboard y administradores quedan con `permitAll()`.
- **Configuración a revisar**: separadores de perfil comentados y claves duplicadas en `application.properties`; Lombok y dependencias de test declaradas dos veces; `spring-boot-starter-data-jpa` 3.5.14 frente al parent 3.5.4.
- **Portal legacy incompleto**: `AdministradorController` devuelve la vista `admin/dashboard`, pero esa plantilla no existe en `resources/templates`; la ruta real del panel es la SPA `/admin/dashboard`.
- **`CorsConfig` no lleva `@Configuration`**; el CORS efectivo de la API depende de los `@CrossOrigin` de cada controlador REST.
- **`ImagenServiceImpl`** escribe fotos en `src/main/resources/static/images/mascotas` en tiempo de ejecución y silencia errores de E/S.
- **`contarTratamientosAplicados()`** cuenta todos los tratamientos, no solo los completados; `DashboardServiceImpl` etiqueta `countByEstado(ACTIVO)` como «mascotas en tratamiento».
- **Validación**: no hay Bean Validation; las reglas se validan a mano en los servicios y con `Validators` en los formularios reactivos.
- **Enlaces simulados**: «Política de Privacidad», «Términos de Servicio», «Protocolo de Emergencia», «¿Olvidaste tu contraseña?», «Agendar cita» (landing y hero) y los enlaces de redes del footer apuntan a `#` sin flujo asociado.
- **Botones decorativos**: el logo, las notificaciones y el menú móvil de los headers de administrador y veterinario no tienen acción real.
- **Funcionalidad de API sin UI**: CRUD de drogas y de administradores, `DELETE /mascota/{id}` (existe en el servicio Angular pero sin botón), `GET /dueno/{id}/mascotas` (la UI usa `/mascota/dueno/{id}`), `GET /dueno/details` y `GET /veterinario/details`.
- **Búsqueda de mascotas** por nombre existe en la API (`GET /mascota/buscar`) pero no se usa en la SPA; los filtros de listados son locales.
- **Detalle de cliente sin `duenoId`**: `/cliente/mascotas` cae a `GET /mascota/all` y mostraría todas las mascotas, no solo las propias.
- **Legacy incompleto**: el login MVC de administrador termina en `return "redirect:";` con comentario `Falta`; existe la plantilla huérfana `templates/veterinario/lista-clientes.html` que ningún controlador devuelve; el portal legacy no tiene módulo de citas y sus enlaces de «cerrar sesión» solo navegan a `/login`.
- **README desactualizado** respecto a Java, herramienta de build, puerto y arquitectura descrita; este documento refleja el código actual.
