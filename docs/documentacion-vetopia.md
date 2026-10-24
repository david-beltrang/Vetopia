# Documentación técnica — Vetopia

Guía de referencia del proyecto: **cómo funciona el frontend Angular** (usando tu código real como ejemplo) y **cómo funciona cada endpoint del backend Spring Boot** (con sus capas y reglas de negocio).

> **Stack**: Angular 19 (standalone, sin NgModules) · RxJS 7.8 · Spring Boot 3.5.4 (Java 17) · Spring Data JPA (Hibernate) · H2 (archivo `mydatabase.mv.db`) · Puerto backend: `8090`.

---

# PARTE 1 — Angular

---

## 1. Panorama general: cómo arranca y cómo fluyen los datos

```
┌─────────────────────────────────────────────────────────────────────┐
│ FRONTEND (Angular, puerto 4200)                                     │
│                                                                     │
│  main.ts ──► bootstrapApplication(AppComponent, appConfig)          │
│                      │                                              │
│                      ▼                                              │
│              app.config.ts (providers: Router + HttpClient)         │
│                      │                                              │
│                      ▼                                              │
│              app.routes.ts ──► Router ──► <router-outlet>           │
│                                          (muestra la page)         │
│                                                                     │
│  Page Component ──inject──► Service Angular ──HttpClient──► Observable<T>
│       │                            │                                │
│       ▼ (subscribe)                │ petición HTTP (JSON)           │
│  Template HTML se renderiza        ▼                                │
│                        http://localhost:8090/...                    │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │ CORS (localhost:4200 permitido)
┌───────────────────────────────────▼─────────────────────────────────┐
│ BACKEND (Spring Boot, puerto 8090)                                  │
│                                                                     │
│  @RestController  ──►  Service (@Service, lógica de negocio)        │
│                              │                                      │
│                              ▼                                      │
│                     Repository (Spring Data JPA)                   │
│                              │                                      │
│                              ▼                                      │
│                     Entity (@Entity) ── Hibernate genera SQL ──► H2 │
│                                                                     │
│  Respuesta JSON (DTO o entidad) ──► Angular la "desempaqueta"       │
└─────────────────────────────────────────────────────────────────────┘
```

**Idea central**: Angular **nunca** toca la base de datos. Solo manda peticiones HTTP y recibe JSON. Toda la lógica de negocio (validaciones, descuento de stock, reglas) vive en el backend. El frontend solo valida campos para UX y renderiza.

---

## 2. `main.ts` — el arranque

```ts
bootstrapApplication(AppComponent, appConfig)
  .catch(err => console.error(err));
```

- `bootstrapApplication` arranca la app standalone (Angular 17+ ya no necesita `AppModule`).
- Le pasa 2 cosas: el **componente raíz** (`AppComponent`) y la **configuración global** (`appConfig`).
- `AppComponent` solo contiene `<router-outlet></router-outlet>`: un marcador de posición donde Angular inserta el componente de la ruta activa.

**Sintaxis**: `.catch(...)` es un `Promise` de JS puro, no RxJS. Sirve solo para loguear errores fatales de arranque.

---

## 3. `app.config.ts` — los providers

```ts
export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient()
  ]
};
```

- **¿Qué es un provider?** Es el registro de un servicio en el "inyector raíz" de Angular, para que cualquier componente pueda pedirlo con `inject()`. En Angular standalone, en vez de declararlos en un `providers: []` de un módulo, se usan funciones `provideX()`.

- `provideZoneChangeDetection({ eventCoalescing: true })`: configura **cómo Angular detecta cambios** en la pantalla. Usa Zone.js: cualquier evento (click, HTTP, timer) dispara una revisión del DOM. `eventCoalescing: true` agrupa eventos del mismo ciclo para no repetir revisiones (menos ciclos de detección = mejor rendimiento). Es el comportamiento por defecto; dejarlo explícito es buena práctica.

- `provideRouter(routes)`: registra el **servicio de ruteo** (`Router`) y le entrega tu array de rutas de `app.routes.ts`. Al navegar (`router.navigate`, `routerLink`), el Router compara la URL con los `path`, instancia el componente correspondiente y lo coloca en el `<router-outlet>`. **SPA**: no recarga la página.

- `provideHttpClient()`: registra `HttpClient` en el inyector raíz. Sin esto, los services no podrían hacer `http.get(...)`.

**Nota** (hallazgo): aquí no se usa `withComponentInputBinding()`, por eso los componentes leen los parámetros de la URL a mano con `route.snapshot` en `ngOnInit` en vez de recibirlos como inputs automáticos.

---

## 4. `app.routes.ts` — las rutas

```ts
export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'login', component: LoginPageComponent, title: 'Vetopia - Iniciar Sesión' },
  { path: 'veterinario/mascotas/ficha/:id', component: FichaClinicaComponent },
  { path: 'veterinario/mascotas/registrar-mascota/:duenoId', component: RegistrarMascotaComponent },
  { path: 'cliente/mascotas/detalle', component: DetalleMascotaComponent },
  { path: 'error', component: ErrorPageComponent },
  { path: '**', redirectTo: 'veterinario/mascotas' }
];
```

Conceptos:

- **`path` con `:parametro`** = **path param** (parte de la ruta). Ej: `ficha/:id` matchea `ficha/5` → `id = "5"`. Sirve para **identificar un recurso** (una mascota, un dueño). Se lee con:
  ```ts
  const id = this.route.snapshot.params['id'];  // string
  ```

- **Query params** = la cola `?clave=valor`, ej: `?veterinarioId=3`. Sirven para **contexto adicional** (quién está logueado, filtros). Se leen con:
  ```ts
  this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;
  ```

- **Convención del proyecto**: los **ids de entidad** van en *path params*; el **usuario logueado** (`veterinarioId` / `duenoId`) va siempre en *query params*. No hay guards ni sesión real: "la sesión viaja en la URL" y se propaga a mano en cada `navigate(..., { queryParams: {...} })`.

- **`title`**: actualiza `document.title` del navegador al navegar.

- **`pathMatch: 'full'`**: la ruta debe coincidir exactamente (para rutas vacías o ramas).

- **`path: '**'` (wildcard)**: lo que no matchee ninguna ruta redirige a `/veterinario/mascotas`. Es el "404" de Angular.

- **`redirectTo`**: redirige sin renderizar componente propio.

**Sintaxis extra de rutas usada en el proyecto** — `history.state`:
```ts
this.router.navigate(['/ruta'], { state: { resultado } });   // en origen
const state = (history.state ?? {}) as { resultado?: X };    // en destino
```
Permite pasar un objeto grande (ej. el `ResultadoTratamientoDTO` de la confirmación de tratamiento) **sin** que aparezca en la URL. Es un patrón de "flash data": si el usuario recarga, `history.state` queda vacío.

---

## 5. `models/` — interfaces que espejean el backend

Los modelos son **interfaces TypeScript**: contratos de forma (qué campos y qué tipos tiene un JSON), **no clases**. Angular no hace `new Mascota()`; solo describe cómo luce el JSON que llega del backend.

```ts
// src/app/models/mascota.model.ts
export interface Mascota {
  id?: number;            // opcional: no existe antes de guardarlo
  nombre: string;
  especie: Especie;       // referencia a un enum
  raza: string;
  edad: number;
  sexo: Sexo;
  imagen?: string;
  pesoKg: number;
  color: string;
  fechaIngreso: string;   // LocalDate se serializa como string "2026-10-18"
  duenoId?: number;       // referencia al dueño POR ID (no objeto embebido)
  estado: Estado;
  tratamientos?: Tratamiento[];   // relación inversa, opcional
}
```

Puntos clave:

- **`?` = campo opcional**. `id?: number` porque antes de `POST` no existe; después del `GET`, el backend lo manda.
- **Relaciones**: cuando la relación es "por id" se guarda un número (`duenoId?: number`); cuando es el objeto completo se usa el tipo de otra interfaz (`mascota?: Mascota` dentro de `Tratamiento`).
- **DTOs del backend también tienen modelo en el front**: `TratamientoDetalle` espejea a `HistorialTratamientoDTO` de Java; `ResultadoTratamientoDTO` espejea al homónimo Java; `ResultadoLogin` a `ResultadoLogin` de Java.

### Enums como *union types*

```ts
// src/app/models/enums/estado.enum.ts
export type Estado = 'Activo' | 'Inactivo';
export const ESTADOS: Estado[] = ['Activo', 'Inactivo'];
```

`'Activo' | 'Inactivo'` es una **union type**: solo acepta esas dos cadenas exactas. El `const ESTADOS` es el array que se recorre en los `<select>` de los formularios. Los valores coinciden con los enums del backend (`DominioEstado.ACTIVO` → `"Activo"`), para que el JSON viaje ida y vuelta sin mapeo.

- `Especie = 'Perro' | 'Gato' | 'Otro'`
- `Sexo = 'Macho' | 'Hembra'`
- `Estado = 'Activo' | 'Inactivo'`

---

## 6. `service/` — la capa de comunicación HTTP

Un service Angular es una clase con `@Injectable` cuyo trabajo es **encapsular las peticiones HTTP** y devolverlas como `Observable<T>`.

```ts
// src/app/service/tratamiento.service.ts (versión reducida)
@Injectable({ providedIn: 'root' })
export class TratamientoService {
  private http = inject(HttpClient);
  private readonly url = `${API_URL}/tratamiento`;   // http://localhost:8090/tratamiento

  listarHistorialDeMascota(mascotaId: number): Observable<TratamientoDetalle[]> {
    return this.http.get<TratamientoDetalle[]>(`${this.url}/mascota/${mascotaId}/historial`);
  }

  asignarTratamiento(tratamiento: TratamientoAsignacion, veterinarioId: number)
      : Observable<ResultadoTratamientoDTO> {
    return this.http.post<ResultadoTratamientoDTO>(
      `${this.url}/asignar`, tratamiento, { params: { veterinarioId } });
  }
}
```

Desglose de sintaxis:

- **`@Injectable({ providedIn: 'root' })`**: registra el service como *singleton* en el inyector raíz. Cualquier componente lo pide con `inject(TratamientoService)` y recibe **la misma instancia**. No hay que declararlo en ningún módulo ni config.
- **`inject(HttpClient)`**: forma moderna de inyección de dependencias (Angular 14+), se usa en el *field* de la clase sin constructor.
- **`Observable<T>`**: el tipo de retorno. **No es el dato**: es una "promesa reactiva" que emitirá el dato `T` cuando la respuesta HTTP llegue. Ver sección 8.
- **`http.get<T>(url)` / `http.post<T>(url, body, opts)`**: devuelven `Observable<T>`; el `<T>` es solo una promesa de tipos ("cuando llegue, será de este tipo").
- **`{ params: { veterinarioId } }`**: agrega query params a la URL → `...?veterinarioId=3`.
- **`API_URL`** viene de `config/api.config.ts`: `export const API_URL = 'http://localhost:8090';` — un solo lugar para cambiar la URL base.

### Inventario de services y sus métodos

| Service | Método | Petición HTTP | Qué recibe | Qué retorna |
|---|---|---|---|---|
| **AuthService** | `login({correo, contrasena})` | `POST /auth/login` | credenciales (cuerpo) | `Observable<ResultadoLogin>` |
| **DuenoService** | `listarDuenos()` | `GET /dueno/all` | — | `Observable<Dueno[]>` |
| | `obtenerDuenoPorId(id)` | `GET /dueno/{id}` | id | `Observable<Dueno>` |
| | `listarMascotasDeDueno(id)` | `GET /dueno/{id}/mascotas` | id | `Observable<Mascota[]>` |
| | `crearDueno(dueno)` | `POST /dueno` | cuerpo `Dueno` | `Observable<Dueno>` (con id) |
| | `actualizarDueno(id, dueno)` | `PUT /dueno/{id}` | id + cuerpo | `Observable<Dueno>` |
| | `eliminarDueno(id)` | `DELETE /dueno/{id}` | id | `Observable<void>` |
| **MascotaService** | `listarMascotas()` | `GET /mascota/all` | — | `Observable<Mascota[]>` |
| | `obtenerMascotaPorId(id)` | `GET /mascota/{id}` | id | `Observable<Mascota>` |
| | `listarMascotasDeDueno(duenoId)` | `GET /mascota/dueno/{duenoId}` | duenoId | `Observable<Mascota[]>` |
| | `buscarPorNombre(nombre)` | `GET /mascota/buscar?nombre=` | nombre | `Observable<Mascota[]>` *(sin uso en pages)* |
| | `crearMascota(mascota, duenoId)` | `POST /mascota?duenoId=` | cuerpo + duenoId | `Observable<Mascota>` |
| | `actualizarMascota(id, mascota)` | `PUT /mascota/{id}` | id + cuerpo | `Observable<Mascota>` |
| | `cambiarEstado(id, estado)` | `PUT /mascota/{id}/estado?estado=` | id + estado | `Observable<void>` |
| | `eliminarMascota(id)` | `DELETE /mascota/{id}` | id | `Observable<void>` *(sin uso)* |
| **VeterinarioService** | `listarVeterinarios()` | `GET /veterinario/all` | — | `Observable<Veterinario[]>` |
| | `obtenerVeterinarioPorId(id)` | `GET /veterinario/{id}` | id | `Observable<Veterinario>` |
| | `obtenerVeterinarioOpcional(id)` | `GET /veterinario/{id}` si `id > 0`, si no **sin petición** | id | `Observable<Veterinario \| undefined>` |
| **DrogaService** | `listarDrogas()` | `GET /droga/all` | — | `Observable<Droga[]>` |
| | `obtenerDrogaPorId(id)` | `GET /droga/{id}` | id | `Observable<Droga>` *(sin uso)* |
| **TratamientoService** | `listarHistorialDeMascota(mascotaId)` | `GET /tratamiento/mascota/{id}/historial` | mascotaId | `Observable<TratamientoDetalle[]>` |
| | `listarHistorialDeVeterinario(veterinarioId)` | `GET /tratamiento/veterinario/{id}/historial` | veterinarioId | `Observable<TratamientoDetalle[]>` |
| | `asignarTratamiento(tratamiento, veterinarioId)` | `POST /tratamiento/asignar?veterinarioId=` | cuerpo + vetId | `Observable<ResultadoTratamientoDTO>` |
| | `actualizarEstado(id, completado)` | `PUT /tratamiento/{id}/estado?completado=` | id + booleano | `Observable<Tratamiento>` |

**Detalle curioso — `obtenerVeterinarioOpcional`** (en `veterinario.service.ts`):

```ts
obtenerVeterinarioOpcional(id: number): Observable<Veterinario | undefined> {
  if (id > 0) {
    return this.obtenerVeterinarioPorId(id);   // petición HTTP real
  }
  return of(undefined);                        // observable vacío, sin HTTP
}
```
Es defensivo: si no hay `veterinarioId` en la URL (recarga limpiada, etc.), evita un `GET /veterinario/0` que daría 404 y rompería el `combineLatest`. Aquí aparece **`of()`**, que se explica en RxJS.

---

## 7. Pages vs. componentes

El proyecto distingue dos niveles:

| | **Pages** (`src/app/pages/**`) | **Componentes / utils** (`components/`, `utils/`) |
|---|---|---|
| ¿Tiene ruta? | Sí, en `app.routes.ts` | No; se usan dentro del template de una page |
| ¿Hace peticiones HTTP? | Sí (usan services + RxJS) | **No** |
| ¿Tiene lógica? | Sí: `ngOnInit`, `subscribe`, navegación | Mínima: mostrar datos, emitir eventos |
| Comunicación | Inyecta services | Recibe `input()`, emite `output()` |

Ejemplo de la jerarquía de una pantalla:

```
FichaClinicaComponent (page, tiene ruta, hace HTTP)
  ├── HeaderVeterinarioComponent        (input: veterinario, veterinarioId)
  ├── EstadoPacienteComponent           (input: mascota → output: estadoCambiado)
  ├── DatosMascotaComponent             (input: mascota)
  ├── DatosPropietarioComponent         (input: propietario)
  └── HistorialMedicoComponent          (input: historial[])
        └── ItemTratamientoComponent    (input: item)   ← "hoja", solo muestra
```

### Inputs y outputs (sintaxis signals de Angular 19)

Ya **no** se usa `@Input()`/`@Output()`/`EventEmitter`. El proyecto usa las funciones `input()` y `output()`:

```ts
// HIJO recibe datos
export class ItemTratamientoComponent {
  item = input<TratamientoDetalle>();          // input opcional
  // item = input.required<TratamientoDetalle>(); // input obligatorio
}

// HIJO emite eventos
export class FormularioTratamientoComponent {
  guardar = output<TratamientoFormValue>();    // emite un objeto tipado
  cancelar = output<void>();                   // emite "ya, sin payload"
}
```

```html
<!-- PADRE los conecta en su template -->
<app-formulario-tratamiento
  [mascota]="mascota"                 <!-- binding de propiedad: [x] = de padre a hijo -->
  [drogas]="drogas"
  (guardar)="onGuardar($event)"       <!-- binding de evento: (x) = de hijo a padre -->
  (cancelar)="onCancelar()">
</app-formulario-tratamiento>
```

- `[propiedad]="expresion"` → pasa **datos** del padre al hijo. Si el valor cambia, el hijo se actualiza (change detection).
- `(evento)="handler($event)"` → el hijo **notifica** al padre cuando pasa algo. `$event` es el payload que el hijo emitió con `this.guardar.emit(valor)`.
- `seccionActiva="clientes"` (sin corchetes) → string **literal**, igual que en HTML normal.

### `signal()` — estado reactivo simple

Tres páginas lo usan (`login-page`, `asignar-tratamiento`, `tratamientos`):

```ts
mensajeError = signal('');        // declara
this.mensajeError.set('x');       // muta
{{ mensajeError() }}              // lee en template (¡con paréntesis!)
```

Una *signal* es un valor reactivo: al hacer `.set()`, Angular sabe exactamente qué vistas dependen de él y las actualiza. En el template **siempre se lee con `()`**, como si fuera una función.

---

## 8. RxJS — qué es y todo lo que usa el proyecto

### ¿Qué es un Observable?

Un `Observable<T>` es un **flujo asíncrono de valores de tipo `T`**. Piensa en él como una "cita para recibir datos que aún no llegan":

- `http.get<Mascota>(url)` no te da la mascota: te da un `Observable<Mascota>` — una promesa de que **habrá** una mascota.
- El dato llega (o falla) **después**, cuando el servidor responde.
- Para **recoger** el valor tienes que suscribirte: `.subscribe(...)`.

**¿Por qué RxJS y no `async/await`?** Porque en UI casi nunca te basta *una* respuesta: necesitas **combinar varias en paralelo**, **encadenar** una que depende de otra, **condicionar** ramas... RxJS da operadores para componer flujos asíncronos declarativamente. `await` encadena "de a una" con callbacks anidados; RxJS lo describe como tubería.

El `Observable` es **frío y perezoso**: nada pasa hasta que alguien hace `.subscribe()`. Y los observables de `HttpClient` **completan** tras emitir 1 valor (o error), por eso no hace falta desuscribirse (no hay *memory leaks*).

### Los operadores que usa tu proyecto (y solo esos)

#### 1. `subscribe({ next, error })` — consumir el flujo

Patrón **universal** del proyecto (26 llamadas): siempre objeto observer, nunca función anónima suelta.

```ts
this.mascotaService.obtenerMascotaPorId(id).subscribe({
  next: (mascota) => { this.mascota = mascota; },     // llegó el dato
  error: () => this.router.navigate(['/error'])       // falló la petición
});
```

- `next(dato)`: se ejecuta cuando emite el valor (respuesta 2xx).
- `error(err)`: se ejecuta si algo falla (404, 500, sin conexión). **Corta el flujo**: lo que venga después no se ejecuta.
- **Excepciones del patrón** (errores de negocio esperados, no fallos técnicos):
  - `login-page`: un 401 hace `mensajeError.set('Credenciales incorrectas.')`.
  - `asignar-tratamiento`: si el backend rechaza por falta de stock, se muestra `err.error.message` en `mensajeError` con el texto exacto (p. ej. "El medicamento X no tiene unidades disponibles") en vez de navegar a `/error`. Solo cae a `/error` si la respuesta no trae mensaje.

#### 2. `combineLatest({...})` — esperar varias peticiones **en paralelo**

Cuando los datos son **independientes**, se piden juntos y se espera a los dos.

```ts
// listar-cliente.component.ts
combineLatest({
  clientes:    this.duenoService.listarDuenos(),
  veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
}).subscribe({
  next: ({ clientes, veterinario }) => {          // desestructuración de JS
    this.clientes = clientes;
    this.veterinario = veterinario;
  },
  error: () => this.router.navigate(['/error'])
});
```

- **Qué espera**: un objeto `{ clave: Observable }`. Emite un objeto `{ clave: valor }` **cuando todos han emitido al menos una vez**.
- **Qué produce**: un solo objeto con las respuestas ya combinadas → un solo `subscribe` en vez de 2-3 con banderas de "ya cargó".
- Forma objeto (no array): los nombres de las claves documentan qué es cada cosa.
- Otros usos: `mascotas` (3 fuentes: mascotas+dueños+veterinario), `editar-cliente`, `detalle-cliente`, `ficha-clinica` (×2, anidado), `asignar-tratamiento` (3 fuentes).

#### 3. `mergeMap` — encadenar una petición que **depende** de la anterior

Cuando el segundo HTTP necesita un dato del primero (ej: "el historial de *esta* mascota"), no puedes usar `combineLatest`: primero va A, después B(A).

```ts
// detalle-mascota.component.ts
this.mascotaService.obtenerMascotaPorId(id).pipe(
  mergeMap(mascota =>                                  // llega la mascota...
    this.tratamientoService.listarHistorialDeMascota(id).pipe(
      map(historial => ({ mascota, historial }))       // ...después el historial
    )
  )
).subscribe({ next: ({ mascota, historial }) => { ... } });
```

- **Qué hace**: por cada valor que emite el observable fuente, ejecuta la función y **aplana** el `Observable` nuevo que ésta devuelve (de `Observable<Mascota>` a `Observable<Historial[]>` → se "fusiona" a un solo nivel).
- **Qué espera**: que la función devuelva un `Observable`.
- **Qué produce**: los valores del observable interno, con la data externa ya encapsulada en el objeto.
- Alternativas que NO se usan: `switchMap` cancelaría la petición anterior si llega un valor nuevo (ideal para búsquedas con `debounceTime`); `concatMap` las serializaría estrictamente. Como aquí cada entrada dispara una sola vez, `mergeMap` sobra.

#### 4. `map` — reempaquetar (no confundir con array.map)

```ts
map(historial => ({ mascota, historial }))   // {mascota, historial} → {mascota, historial}
```

- Transforma **cada valor** que pasa por el flujo. En el proyecto **solo** se usa para empaquetar datos previos + nuevos en un objeto antes del `subscribe`, y así evitar variables intermedias y anidar menos callbacks.
- RxJS `map` ≠ `Array.prototype.map`: opera sobre un valor **asíncrono**, no sobre un array.

#### 5. `of(valor)` — emitir un valor síncrono (placeholder condicional)

```ts
// ficha-clinica.component.ts — dentro de combineLatest
propietario: mascota.duenoId != null
  ? this.duenoService.obtenerDuenoPorId(mascota.duenoId)
  : of<Dueno | undefined>(undefined)      // ← sin petición, emite undefined ya
```

- **Qué hace**: crea un observable que emite `valor` **de inmediato** y completa.
- **Para qué**: en una rama condicional, si faltara el observable, el `combineLatest` **nunca emitiría** (espera a *todas* sus fuentes) y la pantalla quedaría cargando eternamente. `of(undefined)` es el relleno que mantiene viva la tubería.
- También lo usa `VeterinarioService.obtenerVeterinarioOpcional` cuando no hay id.

#### 6. `pipe(...)` — tubería para encadenar operadores

```ts
observableFuente.pipe(mergeMap(...), map(...)).subscribe(...)
```

`pipe` junta operadores en orden: lo que sale de uno entra al siguiente. Es simplemente la "cola de ensamblaje" de un flujo.

### Patrones de composición identificados (los 3 estilos)

1. **Paralelo** — `combineLatest({a$, b$}).subscribe(...)` → datos independientes (cabeceras + listados).
2. **Secuencial** — `a$.pipe(mergeMap(x => b$(x).pipe(map(y => ({...}))))).subscribe(...)` → dependencias reales (dueño según mascota, historial según mascota).
3. **Mixto (el más complejo)** — `combineLatest` → `mergeMap` → `combineLatest` anidado con rama `of(undefined)`: **`ficha-clinica`** y **`asignar-tratamiento`**.

### RxJS desarmado: los 2 flujos más complejos

**`ficha-clinica.component.ts` — `cargarFicha(id)`**

```ts
combineLatest({                                                    // FASE 1 (paralelo)
  mascota:      this.mascotaService.obtenerMascotaPorId(id),
  veterinario:  this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
}).pipe(
  mergeMap(({ mascota, veterinario }) =>                           // FASE 2 (secuencial)
    combineLatest({
      historial:   this.tratamientoService.listarHistorialDeMascota(id),
      propietario: mascota.duenoId != null                          // depende de la mascota
        ? this.duenoService.obtenerDuenoPorId(mascota.duenoId)
        : of<Dueno | undefined>(undefined)                          // relleno condicional
    }).pipe(
      map(({ historial, propietario }) =>                          // FASE 3 (reempaquetar)
        ({ mascota, veterinario, historial, propietario }))
    )
  )
).subscribe({
  next: ({ mascota, veterinario, historial, propietario }) => {    // 1 solo next con los 4
    this.mascotaSeleccionada = mascota;
    this.veterinario = veterinario;
    this.propietarioSeleccionado = propietario;
    this.historialSeleccionado = historial;
    this.cargando = false;
  },
  error: () => this.router.navigate(['/error'])
});
```

| Fase | Operador | Espera | Produce |
|---|---|---|---|
| 1 | `combineLatest` (objeto) | 2 observables independientes | `{mascota, veterinario}` cuando ambos responden |
| 2 | `mergeMap` | función que devuelva un `Observable` | aplana el flujo interno |
| 2 | `combineLatest` (anidado) | historial + dueño (paralelos, 2.ª tanda) | `{historial, propietario}` |
| 3 | `map` | función pura de transformación | `{mascota, veterinario, historial, propietario}` |
| — | `of(undefined)` | — | observable inmediato para la rama sin dueño |
| 4 | `subscribe({next, error})` | — | 1 `next` con todo el paquete o navegar a `/error` |

**`asignar-tratamiento.component.ts`** — mismo patrón con **3 fuentes** en la fase 1 (`mascota`, `drogas`, `veterinario`) y luego `mergeMap` con `dueno$` condicional.

### Lo que RxJS NO se usa en el proyecto (verificado)

`switchMap`, `concatMap`, `exhaustMap`, `catchError`, `tap`, `filter`, `debounceTime`, `distinctUntilChanged`, `take*`, `retry`, `timeout`, `share`, `forkJoin`, `zip`, `from`, `interval`, `timer`, `delay`, cualquier `Subject` (`BehaviorSubject`, etc.), `toSignal`, y el **`async` pipe** en templates (0 apariciones).

Implicaciones:
- **Sin `catchError`**: cualquier error HTTP rompe el flujo completo → el patrón general es redirigir a `/error`, salvo los errores de negocio que se muestran inline (login 401 y stock en `asignar-tratamiento`).
- **Sin `async` pipe`**: todo se asigna a propiedades en `next` (`this.mascota = mascota`) en vez de `mascota$ | async` en el template.
- **Sin desuscripción**: es seguro porque los observables de `HttpClient` completan tras 1 valor; no quedan suscripciones colgadas.

---

## 9. Templates: sintaxis usada en los `.html`

```html
<!-- Interpolación: inserta valores en el texto -->
<p>{{ mascotaSeleccionada.nombre }}</p>

<!-- Control flow NUEVO de Angular (reemplaza a *ngIf / *ngFor) -->
@if (cargando) {
  <div>Cargando...</div>
} @else if (mascotaSeleccionada) {
  <h2>{{ mascotaSeleccionada.nombre }}</h2>
} @else {
  <app-mascota-no-encontrada />
}

@for (item of historial(); track item.tratamiento.fecha) {
  <app-item-tratamiento [item]="item" />
} @empty {
  <p>Sin tratamientos registrados.</p>
}
```

- `@if / @else if / @else` → condicionales. Se usa `@if` con **expresión de JS** entre paréntesis.
- `@for (x of lista; track identificador) { }` → iteración. `track` le dice a Angular **qué campo identifica a cada fila** (rendimiento + `@empty` funciona).
- `@empty` → se muestra si la colección está vacía.
- `{{ expresion }}` → interpolación de texto.
- **No hay ni un `*ngIf`/`*ngFor` en todo el proyecto** (es la sintaxis legacy).

Más bindings:

```html
<app-header [veterinario]="veterinario" seccionActiva="clientes">  <!-- prop + literal -->
<button (click)="navigateToMascotas()">Ir</button>                  <!-- evento DOM -->
<a [routerLink]="['/veterinario/clientes']">Clientes</a>           <!-- link SPA -->
@if (mensajeError()) { <p>{{ mensajeError() }}</p> }               <!-- signal -->
```

### Forms reactivos

Viven en componentes hijos (`formulario-tratamiento`, `formulario-cliente`, `formulario-mascota`, `formulario-login`):

```ts
form = new FormGroup({
  fecha:   new FormControl(fechaHoy(), { nonNullable: true, validators: [Validators.required] }),
  drogaId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  ...
});

onGuardar(): void {
  if (this.form.invalid) {
    this.form.markAllAsTouched();   // fuerza a mostrar los errores
    return;
  }
  const valor = this.form.getRawValue();
  this.guardar.emit({ fecha: valor.fecha, drogaId: Number(valor.drogaId), ... });
}
```

```html
<form [formGroup]="form" (ngSubmit)="onGuardar()">
  <input formControlName="titulo" />
  @if (form.get('titulo')?.touched && form.get('titulo')?.invalid) {
    <span>El título es obligatorio</span>
  }
</form>
```

- `FormGroup` agrupa `FormControl` (cada campo del form).
- `Validators.required` → validación **del lado cliente (UX)**. El backend **también** valida: esa duplicación es intencional (defensa en profundidad: mejor UX + seguridad).
- `markAllAsTouched()` marca todos los campos como "tocados" para que se muestren los mensajes de error aunque el usuario no haya pisado el campo.
- `getRawValue()` extrae todos los valores del form tipados.
- El hijo **no** llama al service: solo `emit` con los datos, y la **page** (`asignar-tratamiento`) decide qué hacer con ellos (llamar HTTP, navegar). Ese es el patrón padre↔hijo.

### Stock de medicamentos en el formulario de tratamiento

El selector de droga de `FormularioTratamientoComponent` trabaja con el campo `unidadesDisponibles` de cada `Droga` (ya viene en `GET /droga/all`):

- Cada `<option>` muestra `Nombre — N disponibles` o `Nombre — Agotado`, y las drogas con `unidadesDisponibles <= 0` llevan `[disabled]`, por lo que no se pueden seleccionar.
- Debajo del selector se muestra el stock de la droga elegida: "Quedan N unidades disponibles" (`text-primary`) o "Este medicamento no tiene unidades disponibles" (`text-error`, `role="alert"`).
- Un **validador de respaldo** (`stockDisponibleValidator`) marca `drogaId` como inválido si apuntara a una droga agotada, de modo que el botón "Aplicar Tratamiento" queda deshabilitado (`form.invalid`).
- El envío sigue siendo responsabilidad de la page: `AsignarTratamientoComponent.onGuardar` llama a `POST /tratamiento/asignar`.
- **Red de seguridad (condición de carrera)**: si el stock se agotó entre que se cargó el formulario y se guardó, el backend responde 400 con `ErrorResponse.message`; la page lee `err.error.message` y lo muestra en el bloque `mensajeError`, sin salir de la pantalla. Solo navega a `/error` si no hay mensaje.

```ts
// formulario-tratamiento.component.ts (resumen)
drogaId: new FormControl('', {
  nonNullable: true,
  validators: [Validators.required, stockDisponibleValidator(this.drogas)]
});

drogaSeleccionada(): Droga | undefined {
  const id = Number(this.form.controls.drogaId.value);
  return this.drogas().find(droga => droga.id === id);
}
```

---

## 10. Tabla puente: qué endpoint consume cada page

| Page (ruta) | RxJS | Endpoints que consume |
|---|---|---|
| `landing` | — | ninguno |
| `login` | `subscribe` | `POST /auth/login`; `GET /veterinario/all` (solo rol ADMIN) |
| `registrar-cliente` | `subscribe` ×2 | `GET /veterinario/{id}`, `POST /dueno` |
| `registrar-mascota` | `subscribe` ×3 | `GET /veterinario/{id}`, `GET /dueno/{id}`, `POST /mascota?duenoId=` |
| `listar-cliente` | `combineLatest` | `GET /dueno/all`, `GET /veterinario/{id}` |
| `listar-mascota` | `subscribe` | `GET /mascota/dueno/{id}` **o** `GET /mascota/all` |
| `detalle-cliente` | `combineLatest` + `mergeMap` + `map` | `GET /dueno/{id}`, `GET /veterinario/{id}`, `GET /dueno/{id}/mascotas`, `DELETE /dueno/{id}` |
| `editar-cliente` | `combineLatest` | `GET /dueno/{id}`, `GET /veterinario/{id}`, `PUT /dueno/{id}` |
| `editar-mascota` | `subscribe` ×3 | `GET /veterinario/{id}`, `GET /mascota/{id}`, `PUT /mascota/{id}` |
| `detalle-mascota` (cliente) | `mergeMap` + `map` | `GET /mascota/{id}`, `GET /tratamiento/mascota/{id}/historial` |
| `ficha-clinica` | `combineLatest` ×2 + `mergeMap` + `map` + `of` | `GET /mascota/{id}`, `GET /veterinario/{id}`, `GET /tratamiento/mascota/{id}/historial`, `GET /dueno/{id}`, `PUT /mascota/{id}/estado?estado=` |
| `mascotas` (vet) | `combineLatest` ×3 | `GET /mascota/all`, `GET /dueno/all`, `GET /veterinario/{id}` |
| `asignar-tratamiento` | `combineLatest` ×3 + `mergeMap` + `map` + `of` | `GET /mascota/{id}`, `GET /droga/all`, `GET /veterinario/{id}`, `GET /dueno/{id}`, `POST /tratamiento/asignar?veterinarioId=` |
| `tratamientos` (vet) | `subscribe` ×2 + `subscribe` por cada toggle | `GET /veterinario/{id}`, `GET /tratamiento/veterinario/{id}/historial`, `PUT /tratamiento/{id}/estado?completado=` |
| `tratamiento-confirmacion` | `subscribe` ×2 + `history.state` | `GET /veterinario/{id}`, `GET /mascota/{id}` (datos del DTO llegan por `state`) |
| `error` | — | ninguno |

---

# PARTE 2 — Backend: capas y endpoints

---

## 11. Las capas del backend (arquitectura por capas)

```
Petición HTTP
     │
     ▼
┌─────────────────────────────┐
│ @RestController (rest/)     │  Recibe la HTTP, extrae parámetros/JSON,
│  • @GetMapping, @PostMapping│  llama al service y devuelve JSON.
│  • @PathVariable, @RequestParam, │  NO tiene lógica de negocio.
│  • @RequestBody             │  Si el service lanza excepción, NO hace
└──────────────┬──────────────┘  try/catch: el Advice la intercepta.
               │ @Autowired
               ▼
┌─────────────────────────────┐
│ Service (service/…Impl)     │  ⭐ Toda la LÓGICA DE NEGOCIO:
│  • @Service                 │  validaciones, reglas, transacciones
│  • Interfaz + Impl          │  (@Transactional), orquestación entre
│  • Valida params            │  repositories.
│  • Lanza excepciones ───────┼──► RecursoNoEncontradoException → 404
│                             │    ParametroInvalidoException  → 400
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│ Repository (repository/)    │  Interfaz que extiende
│  • JpaRepository<Ent, Long> │  `JpaRepository`. Spring Data JPA
│  • findByMascotaId(id)      │  **genera la implementación y el SQL**
│    (consulta derivada)      │  a partir del nombre del método.
└──────────────┬──────────────┘
               │
               ▼
┌─────────────────────────────┐
│ Entity (@Entity, entities/) │  Mapea una tabla. JPA/Hibernate genera
│  • @Table, @Id, @Column     │  el DDL con `ddl-auto=create-drop`.
│  • @ManyToOne, @OneToMany   │  Las relaciones definen las FKs y las
│  • @OnDelete(CASCADE)       │  cascadas.
└──────────────┬──────────────┘
               │ SQL generado por Hibernate
               ▼
          H2 (archivo ./mydatabase)
```

Conceptos clave de esta arquitectura:

- **Inyección de dependencias** (`@Autowired`): Spring crea y gestiona las instancias (`@Service`, `@RestController`, repositorios); tú solo las pides. La interfaz `TratamientoService` apunta a `TratamientoServiceImpl` — la capa controller depende de la **abstracción**, no de la implementación.
- **Inyección de dependencias**... *(ver glosario)*
- **`@Transactional`**: une varias operaciones de BD en **una sola transacción**. Si algo falla a mitad, **todo se revierte** (rollback). Se usa donde hay más de una escritura: `asignarTratamiento` (guardar tratamiento + descontar stock + actualizar mascota), `eliminarCliente`, `registrarMascota`, `descontarUnidades`, etc.
- **Consultas derivadas**: `findByMascotaId(Long)` no se implementa a mano — Spring Data **interpreta el nombre** (`findBy` + `Mascota` + `Id`) y genera `SELECT * FROM tratamientos WHERE mascota_id = ?`.
- **Cascadas**: `@OnDelete(action = CASCADE)` en las FKs de `Tratamiento` + `cascade = CascadeType.ALL` en las colecciones `@OneToMany` hacen que al borrar un dueño se borren sus mascotas, y al borrar una mascota sus tratamientos.
- **JSON**: Spring (Jackson) serializa las entidades/DTOs a JSON en la respuesta y deserializa el `@RequestBody` a objetos Java en la petición.

### Manejo de excepciones (sin try/catch en controllers)

`RestExceptionHandler` (`@RestControllerAdvice(basePackages = "com.vetopia.rest")`) intercepta las excepciones lanzadas por cualquier controller REST y arma la respuesta:

| Excepción | Status | Cuándo se lanza |
|---|---|---|
| `RecursoNoEncontradoException` | **404** | El id no existe en la BD (`findById(...).orElseThrow(...)`) |
| `ParametroInvalidoException` | **400** | Datos inválidos: id nulo, campos obligatorios, relación sin seleccionar |
| `CredencialesInvalidasException` | **401** | El login no coincide con ninguna tabla (`LoginRestController`) |
| `Exception` (catch-all) | **500** | Lo no anticipado |

El cuerpo siempre es un `ErrorResponse` uniforme:
```json
{ "timestamp": "...", "status": 404, "error": "Not Found",
  "message": "No se encontró ninguna mascota con el identificador 99.",
  "path": "/mascota/99" }
```

**Nota**: hay un segundo advice, `GlobalExceptionHandler` (`basePackages = "com.vetopia.controller"`), que hace lo mismo pero devolviendo la vista HTML `error` (Thymeleaf) — es el legado del portal con vistas; Angular solo recibe del `RestExceptionHandler`. Solo el advice REST maneja `CredencialesInvalidasException` (401): los controllers con vistas no lo usan.

### CORS

No hay clase `@Configuration` global: **cada `@RestController` declara**
```java
@CrossOrigin(origins = "http://localhost:4200")
```
Esto permite que el navegador de Angular (puerto 4200) reciba respuestas del backend (puerto 8090). Sin esto, el navegador bloquearía la respuesta por *same-origin policy*.

### Swagger / OpenAPI

`@Operation(summary = "...")` (de springdoc-openapi) documenta cada endpoint en la UI de Swagger — es solo anotación, no afecta el runtime.

---

## 12. El patrón CRUD (se repite en 6 controllers)

El **80% de los 42 endpoints** es este patrón idéntico. Se explica una vez y luego solo se lista dónde aplica.

```java
@RestController
@RequestMapping("/dueno")                    // prefijo de la clase
@CrossOrigin(origins = "http://localhost:4200")
public class DuenoRestController {

    @Autowired
    private DuenoService duenoService;      // inyección de la capa de negocio

    // LISTAR TODO — GET /dueno/all
    @GetMapping("/all")
    public List<Dueno> listar() {
        return duenoService.listarDuenos();     // → repository.findAll()
    }

    // OBTENER POR ID — GET /dueno/{id}
    @GetMapping("/{id}")
    public Dueno buscarPorId(@PathVariable Long id) {   // {id} → argumento Java
        return duenoService.obtenerDuenoPorId(id);      // valida + findById().orElseThrow()
    }

    // CREAR — POST /dueno
    @PostMapping
    public Dueno agregar(@RequestBody Dueno dueno) {    // JSON del body → objeto Java
        dueno.setId(null);                              // fuerza INSERT (no UPDATE)
        return duenoService.guardar(dueno);             // → repository.save()
    }

    // ACTUALIZAR — PUT /dueno/{id}
    @PutMapping("/{id}")
    public Dueno actualizar(@PathVariable Long id, @RequestBody Dueno dueno) {
        dueno.setId(id);                                // con id → JPA hace UPDATE (merge)
        duenoService.guardar(dueno);
        return dueno;
    }

    // ELIMINAR — DELETE /dueno/{id}
    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable Long id) {
        duenoService.eliminarCliente(id);               // valida existencia y borra en cascada
    }
}
```

### Sintaxis Spring explicada

| Anotación / técnica | Qué hace | Ejemplo |
|---|---|---|
| `@RestController` | Clase = controller REST; devuelve **JSON** (no vistas) | clase |
| `@RequestMapping("/x")` | Prefijo de ruta para todos los métodos | `@RequestMapping("/dueno")` |
| `@GetMapping("/all")` | Maneja `GET /dueno/all` | método |
| `@PostMapping` | Maneja `POST /dueno` (misma ruta que la clase) | método |
| `@PutMapping("/{id}")` | Maneja `PUT /dueno/5` | método |
| `@DeleteMapping("/{id}")` | Maneja `DELETE /dueno/5` | método |
| `@PathVariable Long id` | Extrae segmento de la URL → argumento Java tipado | `{id}` → `5L` |
| `@RequestParam Long duenoId` | Extrae **query param** → argumento Java | `?duenoId=3` → `3L` |
| `@RequestBody Dueno dueno` | **Jackson** deserializa el JSON del cuerpo → objeto Java | `{"nombre":"Ana"}` → `new Dueno(...)` |
| (retorno) | El valor retornado se serializa a JSON y se manda como respuesta | `Dueno` → `{"id":1,...}` |
| `ResponseEntity<T>` | Control manual del status HTTP (ya no lo usan los controllers; lo aplica `RestExceptionHandler`) | 401 / 400 / 404 / 500 |
| `@Operation(summary=...)` | Documentación Swagger | — |

### Dónde aplica el patrón CRUD completo

| Controller | Rutas base | Endpoints CRUD |
|---|---|---|
| `AdministradorRestController` | `/administrador` | `/all`, `/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `DrogaRestController` | `/droga` | `/all`, `/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `VeterinarioRestController` | `/veterinario` | `/all`, `/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` |
| `DuenoRestController` | `/dueno` | `/all`, `/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` + 1 especial |
| `MascotaRestController` | `/mascota` | `/all`, `/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` + 3 especiales |
| `TratamientoRestController` | `/tratamiento` | `/all`, `/{id}`, `POST`, `PUT /{id}`, `DELETE /{id}` + 6 especiales |
| `LoginRestController` | `/auth` | 0 CRUD — solo `/login` (especial) |

> **Total: 42 endpoints** = ~28 CRUD repetidos + ~14 especiales (los que se detallan abajo).

---

## 13. Endpoints especiales (detalle completo)

### 13.1 `POST /auth/login` — autenticación

| | |
|---|---|
| **Qué lo engatilla** | Page `login` → `AuthService.login({correo, contrasena})` (tras `ngSubmit` del `FormularioLoginComponent`) |
| **Recibe** | Body JSON `LoginRequestDTO`: `{ "correo": "...", "contrasena": "..." }` |
| **Devuelve** | 200 + `ResultadoLogin { id, rol, correo }`, o **401** + `ErrorResponse { message: "Credenciales incorrectas." }` si no matchea |

**Recorrido por las capas:**

```
LoginRestController.login(@RequestBody LoginRequestDTO)  → devuelve ResultadoLogin directo
  → LoginServiceImpl.autenticar(correo, contrasena)
      1. Valida que no vengan nulos/vacíos → si están vacíos: return null (→ 401)
      2. veterinarioService.obtenerPorCorreoYContrasena(...)   → ¿match?
      3. administradorService.obtenerPorCorreoYContrasena(...) → ¿match?
      4. duenoService.obtenerPorCorreoYContrasena(...)         → ¿match?
      5. Si ninguno: return null (→ 401)
  → Si encontró: new ResultadoLogin(id, "VETERINARIO"|"ADMINISTRADOR"|"CLIENTE", correo, contrasena)
  → return resultado  → JSON 200
  → Si vino null: throw new CredencialesInvalidasException(...)
      → RestExceptionHandler.manejarCredencialesInvalidas → 401 + ErrorResponse JSON
```

**Reglas de negocio:**
- Valida credenciales no vacías.
- Busca en 3 tablas **en orden** (vet → admin → dueño): el primer match gana.
- Si no hay match → **401** (no 400): "credenciales incorrectas", no "petición malformada"; el controller lo expresa lanzando `CredencialesInvalidasException`, que `RestExceptionHandler` traduce a 401.
- **Seguridad** (débil, es un proyecto académico): compara la contraseña **en texto plano** y consulta con `findByCorreoIgnoreCaseAndContrasena`. En producción: hash + token JWT + HTTPS.

**Manejo en el front**: el `error` del `subscribe` **no** navega a `/error` sino que muestra `mensajeError.set('Credenciales incorrectas.')` — un 401 es resultado de negocio esperado. En `next`, `ingresarSegunRol(rol, id)` navega según el rol:
- `CLIENTE` → `/cliente/mascotas?duenoId={id}`
- `VETERINARIO` → `/veterinario/mascotas?veterinarioId={id}`
- `ADMINISTRADOR` → no hay pantalla propia: pide `GET /veterinario/all` y entra con el primer veterinario.

**Sintaxis/temas**: controller sin `ResponseEntity` (el status lo fija el handler de excepciones), excepción personalizada para un status específico (`CredencialesInvalidasException` → 401), DTO de entrada, patrón *chain-of-responsibility* ligero (3 intentos secuenciales), estado "la sesión viaja en la URL" (query params, no cookies/JWT).

---

### 13.2 `GET /dueno/{id}/mascotas` — mascotas de un cliente

| | |
|---|---|
| **Qué lo engatilla** | Page `detalle-cliente` → `DuenoService.listarMascotasDeDueno(id)` (encadenado vía `combineLatest` + `mergeMap`) |
| **Recibe** | Path param `id` (del dueño) |
| **Devuelve** | `List<Mascota>` (JSON array de mascotas) |

**Recorrido:**
```
DuenoRestController.listarMascotas(@PathVariable Long id)
  → duenoService.obtenerDuenoPorId(id)        // 1) VALIDACIÓN: si no existe → 404
  → mascotaService.listarMascotasDeCliente(id)
      → mascotaRepository.findByDueno_Id(id)  // consulta derivada: WHERE dueno_id = ?
  → List<Mascota> → JSON
```

**Reglas de negocio:**
- **Valida existencia del dueño antes de listar** (`obtenerDuenoPorId` lanza `RecursoNoEncontradoException` → 404). Así un id de dueño inexistente no devuelve `[]` (que parecería "dueño sin mascotas") sino un 404 claro.

**Sintaxis/temas**: composición de 2 services (validación con uno, query con otro), consulta derivada `findByDueno_Id`, validación *fail-fast*.

---

### 13.3 `GET /mascota/dueno/{duenoId}` — mascotas por dueño (ruta alternativa)

| | |
|---|---|
| **Qué lo engatilla** | Page `listar-mascota` → `MascotaService.listarMascotasDeDueno(duenoId)` (query param `duenoId` en la URL de la page decide cuál de los 2 endpoints usar) |
| **Recibe** | Path param `duenoId` |
| **Devuelve** | `List<Mascota>` |

**Recorrido:**
```
MascotaRestController.listarPorDueno(@PathVariable Long duenoId)
  → mascotaService.listarMascotasDeCliente(duenoId)
      → valida duenoId no nulo → ParametroInvalidoException (400)
      → mascotaRepository.findByDueno_Id(duenoId)
```

**Diferencia con 13.2**: esta **no** valida que el dueño exista (solo que el id no sea nulo); 13.2 sí. Es el mismo resultado final pero con distinto nivel de validación — un ejemplo de inconsistencia que podrías unificar.

**Patrón de la page**: `listar-mascota` elige el observable según query param:
```ts
const peticion = this.duenoId > 0
  ? this.mascotaService.listarMascotasDeDueno(this.duenoId)
  : this.mascotaService.listarMascotas();
peticion.subscribe({...});
```

---

### 13.4 `GET /mascota/buscar?nombre=X` — búsqueda por nombre

| | |
|---|---|
| **Qué lo engatilla** | `MascotaService.buscarPorNombre` — **definido pero sin uso en las pages actuales** |
| **Recibe** | Query param `nombre` (obligatorio) |
| **Devuelve** | `List<Mascota>` |

**Recorrido:**
```
MascotaRestController.buscarPorNombre(@RequestParam String nombre)
  → mascotaService.buscarPorNombre(nombre)
      → valida no nulo/vacío → ParametroInvalidoException (400)
      → nombre.trim()
      → mascotaRepository.findByNombreContainingIgnoreCase(nombre)
         // SELECT * FROM mascotas WHERE LOWER(nombre) LIKE '%x%'
```

**Reglas de negocio:**
- **Búsqueda parcial**: `Containing` → `LIKE %texto%` (coincidencias en cualquier posición).
- **Insensible a mayúsculas**: `IgnoreCase` → `LOWER(nombre)`.
- Trim de espacios antes de buscar.

**Sintaxis/temas**: `@RequestParam` (query param con nombre; si falta, Spring responde 400 automáticamente), consulta derivada con `ContainingIgnoreCase`.

---

### 13.5 `PUT /mascota/{id}/estado?estado=X` — cambiar activo/inactivo

| | |
|---|---|
| **Qué lo engatilla** | Page `ficha-clinica` → `MascotaService.cambiarEstado(id, estado)`, disparado por el output `(estadoCambiado)` de `EstadoPacienteComponent` (`onEstadoCambiado`) |
| **Recibe** | Path param `id` + query param `estado` (`"Activo"` o `"Inactivo"`) |
| **Devuelve** | `void` (200 sin body) |

**Recorrido:**
```
MascotaRestController.cambiarEstado(@PathVariable Long id, @RequestParam String estado)
  → mascotaService.cambiarEstado(id, estado)   @Transactional
      → valida id y estado no nulos → ParametroInvalidoException (400)
      → obtenerMascotaPorId(id) → 404 si no existe
      → mascota.setEstado(DominioEstado.fromValor(estado))
           // "fromValor" convierte String → enum; si el valor es desconocido,
           // lanza excepción (ParametroInvalido → 400)
      → mascotaRepository.save(mascota)  // UPDATE
```

**Reglas de negocio:**
- Solo se admiten los valores exactos del dominio (`Activo`/`Inactivo`); cualquier otro string es 400.
- Transaccional (aunque es 1 sola escritura; consistencia con el resto).

**Front**: `EstadoPacienteComponent` recibe `mascota` por input, muestra el toggle, y **emite la mascota clonada con el estado cambiado** (`estadoCambiado = output<Mascota>()`); la page hace el HTTP. El componente hijo no sabe de servicios.

**Sintaxis/temas**: enum como String en la URL, `fromValor` (factory method de validación de dominio), separar "cambiar un flag" del CRUD completo (`PUT` sobre un sub-recurso en vez de `PUT` de la entidad entera).

---

### 13.6 `GET /tratamiento/mascota/{mascotaId}/historial` — historial clínico de una mascota

| | |
|---|---|
| **Qué lo engatilla** | Pages `ficha-clinica` y `detalle-mascota` → `TratamientoService.listarHistorialDeMascota(id)` |
| **Recibe** | Path param `mascotaId` |
| **Devuelve** | `List<HistorialTratamientoDTO>`: `[{ tratamiento, drogaNombre, clienteNombre }, ...]` |

**Recorrido:**
```
TratamientoRestController.historialDeMascota(@PathVariable Long mascotaId)
  → TratamientoServiceImpl.listarHistorialDeMascota(mascotaId)
      → listarTratamientosPorMascota(mascotaId)            // valida id (400 si nulo)
          → tratamientoRepository.findByMascotaId(mascotaId)
      → por cada tratamiento, arma HistorialTratamientoDTO con:
          - tratamiento (la entidad completa)
          - resolverNombreDroga(t):  t.getDroga().getNombre()  → "Medicamento" si null
          - resolverNombreCliente(t): t.getMascota().getDueno().getNombre() → "No registrado" si null
  → List<HistorialTratamientoDTO> → JSON
```

**Reglas de negocio / diseño:**
- **Se devuelve un DTO, no la entidad cruda**, para que el frontend reciba los nombres (`drogaNombre`, `clienteNombre`) **ya resueltos**: Angular no tiene que recorrer `tratamiento.droga.id → buscar droga` ni exponer las entidades completas `Droga`/`Dueno` solo para mostrar un nombre.
- Fallbacks defensivos: `"Medicamento"` y `"No registrado"` si una relación viene null.
- El `Tratamiento` dentro del DTO **sí** incluye mascota/veterinario/droga embebidos (para el historial); no hay recursión infinita porque `Mascota.tratamientos` está anotado `@JsonIgnore`.

**Front**: el flujo es el **patrón mixto** de RxJS (sección 8): el historial es la fase 2 tras cargar la mascota. `TratamientoDetalle` (interfaz TS) espejea al DTO Java.

**Sintaxis/temas**: DTO de salida, navegación de relaciones (`tratamiento → mascota → dueno`), `@JsonIgnore` contra ciclos JSON, resolución de nombres en la capa service.

---

### 13.7 `GET /tratamiento/veterinario/{veterinarioId}/historial` — todos los pacientes del vet

| | |
|---|---|
| **Qué lo engatilla** | Page `tratamientos` (lista "Tratamientos" del vet) → `TratamientoService.listarHistorialDeVeterinario(veterinarioId)` — 2 `subscribe` paralelos e independientes (este + el veterinario del header) |
| **Recibe** | Path param `veterinarioId` |
| **Devuelve** | `List<HistorialTratamientoDTO>` (mismo DTO del anterior) |

**Recorrido:** idéntico a 13.6 pero con `listarTratamientosPorVeterinarioConDroga(veterinarioId)` → `tratamientoRepository.findByVeterinarioId(...)`, y el `clienteNombre` se resuelve recorriendo `tratamiento → mascota → dueno`.

**Nota**: aquí **no** hay `combineLatest` en la page (las 2 peticiones son independientes y se disparan con 2 `subscribe` sueltos). En `listar-cliente`, para un caso equivalente, sí se usa `combineLatest` — es una **inconsistencia de estilo** del proyecto.

**Front**: cada item se renderiza con `ItemTratamientoVeterinarioComponent`, que además muestra `clienteNombre` (a diferencia del historial de mascota, que no lo muestra).

---

### 13.8 `POST /tratamiento/asignar` — asignar tratamiento (el endpoint con más negocio)

| | |
|---|---|
| **Qué lo engatilla** | Page `asignar-tratamiento` → `TratamientoService.asignarTratamiento(tratamiento, veterinarioId)`, disparado por el output `(guardar)` del `FormularioTratamientoComponent` (validado con `FormGroup`) |
| **Recibe** | Body JSON `Tratamiento` (mínimo) + query param `veterinarioId` |

Body enviado por Angular:
```json
{
  "fecha": "2026-10-18",
  "mascota": { "id": 5 },
  "droga":   { "id": 3 },
  "titulo": "Antibiótico semanal",
  "descripcion": "Dosis de 5ml",
  "completado": false
}
```

| **Devuelve** | `ResultadoTratamientoDTO { tratamiento, mascota, droga, unidadesDescontadas, stockRestante, medicamentoNombre }` |

**Recorrido por las capas:**

```
TratamientoRestController.asignar(@RequestBody Tratamiento, @RequestParam Long veterinarioId)
  → tratamiento.setId(null)                    // fuerza INSERT
  → TratamientoServiceImpl.asignarTratamiento(tratamiento, veterinarioId)   @Transactional
      1. Valida tratamiento != null                                  → 400
      2. Valida mascota y droga con id                               → 400
      3. drogaService.obtenerDrogaPorId(id)                          → 404 si no existe
      4. Valida stock: unidadesDisponibles > 0                       → 400
      5. Resuelve veterinario (query param o body)                   → 400 si ninguno
         veterinarioService.obtenerVeterinarioPorId(...)             → 404 si no existe
      6. completado = false si venía null
      7. guardar(tratamiento)                        → INSERT tratamientos
      8. drogaService.descontarUnidades(drogaId, 1)  @Transactional  → UPDATE drogas
            - unidadesDisponibles = max(0, actuales - 1)
            - unidadesVendidas  += 1
      9. mascotaRepository.findById(...)                             → 400 si no existe
     10. Si estado != ACTIVO → reactivar mascota (setEstado(ACTIVO), save)
     11. return new ResultadoTratamientoDTO(tratamiento, mascota, droga, 1)
            - stockRestante calculado en el constructor desde droga
  → JSON 200 con el DTO
```

**Reglas de negocio (todas en la capa Service):**
1. **Transaccionalidad total**: guardar tratamiento + descontar stock + reactivar mascota ocurren en **una transacción**; si falla cualquiera, todo se revierte (no queda un tratamiento sin descuento ni viceversa).
2. **Stock obligatorio**: no se puede asignar una droga sin unidades.
3. **Descuento de inventario**: 1 unidad por tratamiento; `unidadesVendidas` sube 1.
4. **Mascota reactiva**: si la mascota estaba `INACTIVO`, aplicarle tratamiento la pone en `ACTIVO`.
5. **Completado nace en false** (aunque el front también lo envía — ver hallazgo en Apéndice C).
6. **Veterinario obligatorio**: sin vet (por query param ni body), 400.

**Front — el flujo completo:**
1. `AsignarTratamientoComponent.ngOnInit` carga en paralelo (3 `combineLatest`) mascota + drogas + veterinario, y con `mergeMap` el dueño (`of(undefined)` si no hay).
2. El `FormularioTratamientoComponent` valida (fecha=today, droga, título, descripción requeridos), **muestra el stock de la droga elegida** y deshabilita las agotadas; luego `emit` el `TratamientoFormValue`.
3. `onGuardar` arma el body mínimo y llama `asignarTratamiento`.
4. En `next`: navega a `tratamiento-confirmacion/{mascotaId}` pasando el DTO por `state: { resultado }`.
5. `TratamientoConfirmacionComponent` lee `history.state` (sin HTTP para el DTO) y muestra el modal con medicamento, unidades descontadas y stock restante.

**Stock y errores amigables**: el selector muestra `— N disponibles` / `— Agotado` y bloquea las drogas sin unidades (ver sección 9). Si aun así el backend rechaza por stock (carrera entre cargar el form y guardar), el `error` de `onGuardar` lee `err.error.message` y lo pinta en `mensajeError`, en lugar de navegar a `/error`.

**Sintaxis/temas**: `@Transactional` (atomicidad multi-escritura), orquestación entre 3 services (`DrogaService`, `VeterinarioService`, `MascotaRepository`), DTO de salida con cálculo en constructor (`stockRestante`), `history.state` en Angular (flash data), validación en 2 capas.

---

### 13.9 `PUT /tratamiento/{id}/estado?completado=` — marcar Completado/Pendiente

| | |
|---|---|
| **Qué lo engatilla** | Page `tratamientos` (lista del vet) → `TratamientoService.actualizarEstado(id, completado)`, disparado por el output `(estadoCambiado)` de `ItemTratamientoVeterinarioComponent` |
| **Recibe** | Path param `id` + query param `completado` (`true`/`false`) |
| **Devuelve** | `Tratamiento` actualizado (JSON) |

**Recorrido:**
```
TratamientoRestController.actualizarEstado(@PathVariable Long id, @RequestParam boolean completado)
  → TratamientoServiceImpl.actualizarEstado(id, completado)   @Transactional
      → obtenerTratamientoPorId(id)        // 404 si no existe
      → tratamiento.setCompletado(completado)
      → tratamientoRepository.save(...)    // UPDATE
      → return tratamiento
```

**Reglas de negocio:**
- El estado por defecto de un tratamiento nuevo es **Pendiente** (`completado = false`), fijado en `asignarTratamiento`.
- El endpoint permite **ambos sentidos**: completar (`true`) y reabrir/volver a pendiente (`false`). El antiguo `marcarTratamientoComoCompletado(id)` quedó como atajo que delega en `actualizarEstado(id, true)`.
- Es idempotente y no toca el stock (el descuento ocurrió al asignar).

**Front — patrón page→hijo:**
- `ItemTratamientoVeterinarioComponent` es **presentacional**: recibe `item`, `guardando` y `mensajeError` por `input()`, y al hacer click emite `(estadoCambiado) = { tratamientoId, completado: !esCompletado() }`. No inyecta services (misma convención que `EstadoPacienteComponent`).
- `TratamientosComponent.onEstadoCambiado` hace el HTTP, actualiza `item.tratamiento.completado` en la lista (el badge cambia sin recargar) y, si falla, guarda el mensaje en un mapa de errores por fila (`erroresEstado`).
- El badge Completado/Pendiente de la fila es un `<button>` toggle; mientras el PUT está en vuelo se deshabilita (`guardando`).
- Los `POST /tratamiento` / `PUT /tratamiento/{id}` "puros" (ver 13.10) siguen existiendo, pero el flujo de negocio usa este endpoint específico.

**Sintaxis/temas**: sub-recurso de estado (`PUT /{id}/estado`), `@RequestParam boolean`, output del hijo hacia la page, signals para estado de carga y errores por fila.

---

### 13.10 Endpoints CRUD de tratamiento "puros" (sin descuento)

`POST /tratamiento` y `PUT /tratamiento/{id}` guardan **tal cual** el JSON, **sin** validar stock ni descontar (a diferencia de `/asignar`). Y `DELETE /tratamiento/{id}` borra **sin reponer** el stock ya descontado. El `DELETE` sí valida existencia (404). Son endpoints "de administración" crudos: el flujo real de negocio de la app usa **solo** `/asignar`.

---

### 13.11 Endpoints especiales de mascota (resumen rápido)

- **`POST /mascota?duenoId=`** — valida dueño (404 si no existe), fija `estado = ACTIVO`, valida `color` y `fechaIngreso` (400), guarda foto opcional (`ImagenService`) → `registrarMascota` `@Transactional`.
- **`PUT /mascota/{id}`** — conserva dueño, estado e imagen actuales si el JSON no los trae (merge defensivo contra pérdida de datos al editar).
- **`GET /mascota/dueno/{duenoId}`** — ver 13.3.

---

## 14. Configuración, datos de prueba y hallazgos

### Apéndice A — `application.properties` (relevante)

| Propiedad | Valor | Significado |
|---|---|---|
| `server.port` | `8090` | Puerto del backend (Angular usa 4200) |
| `spring.datasource.url` | `jdbc:h2:file:./mydatabase` | H2 en **archivo** (persiste entre corridas) |
| `spring.jpa.hibernate.ddl-auto` | `create-drop` | **Al arrancar recrea el esquema; al parar lo borra.** Los datos (salvo los que cree `DataLoader`) se pierden en cada reinicio |
| `spring.h2.console` | `/h2` | Consola web para inspeccionar la BD |
| `spring.servlet.multipart.max-file-size` | `5MB` | Límite de subida de fotos |
| `logging.level.org.hibernate.SQL` | `DEBUG` | Imprime cada SQL generado |

### Apéndice B — `DataLoader` (datos de prueba)

`config/DataLoader.java` implementa `CommandLineRunner`: **al arrancar** la app (después de crear el esquema) genera 50 dueños, 100 mascotas, veterinarios, admins, drogas (desde el Excel `drogasVetopia.xlsx` con Apache POI) y tratamientos, con `Random(42L)` (semilla fija = datos reproducibles). Todo dentro de `@Transactional`. Con `create-drop`, **esta carga es la que puebla la BD en cada arranque**.

### Apéndice C — Hallazgos menores (para mejorar)

1. **`completado` y `fecha` los define el front**: en la creación, el backend solo pone `completado=false` si llega `null`; un cliente podría mandar `completado: true` al crear. La regla debería imponerse **incondicionalmente** en el Service. Ya existe el endpoint dedicado `PUT /tratamiento/{id}/estado` para cambiar el estado después de creado (ver 13.9), pero la creación sigue confiando en el body. Igual con `fecha`.
2. **Login en texto plano** (sin hash ni token): acceptable solo con fines académicos.
3. **`provideZoneChangeDetection`**: está importado y usado correctamente en `app.config.ts` (a diferencia de lo que podrías leer en versiones previas de este doc).
4. **Endpoints CRUD sin uso en el front**: `DELETE /mascota/{id}`, `buscarPorNombre`, `obtenerDrogaPorId`, los `PUT/POST` de tratamiento "puros".
5. **Inconsistencia de estilo RxJS**: `editar-cliente` usa `combineLatest` para 2 peticiones independientes; `editar-mascota` y `tratamientos` usan 2 `subscribe` sueltos para un caso equivalente.
6. **Sin `catchError`**: la mayoría de errores 4xx/5xx manda a `/error` genérico. Excepciones ya implementadas: login (401 inline) y `asignar-tratamiento` (mensaje de stock inline, con respaldo a `/error`). Queda pendiente extender este patrón a otros flujos.
7. **Doble `GlobalExceptionHandler`** (vistas Thymeleaf) + `RestExceptionHandler` (JSON): legado del portal original con Spring MVC.

---

## Glosario

| Término | Definición operativa |
|---|---|
| **SPA** (Single Page Application) | App que nunca recarga el navegador: el Router de Angular intercambia componentes dentro de `<router-outlet>`. |
| **Provider** | Registro de un servicio/config en el inyector raíz de Angular; se crea una vez y se comparte (`providedIn: 'root'`). |
| **Inyección de dependencias** | Pedir un objeto en vez de crearlo con `new`: en Angular con `inject(X)`; en Spring con `@Autowired`. El framework gestiona la instancia. |
| **Observable (RxJS)** | Flujo asíncrono de valores. No hay dato hasta que se `subscribe`; `HttpClient` emite 1 valor y completa. |
| **DTO** (Data Transfer Object) | Objeto solo para transportar datos entre capas (Controller↔Front). No es entidad: no se persiste. Ej: `HistorialTratamientoDTO`. |
| **Entidad `@Entity`** | Clase Java mapeada a una tabla de BD por JPA/Hibernate. |
| **Consulta derivada** | Método de repository sin implementación: Spring genera el SQL del nombre (`findByMascotaId` → `WHERE mascota_id = ?`). |
| **`@Transactional`** | Une varias operaciones de BD en 1 transacción: o todo, o nada (rollback). |
| **CORS** | Mecanismo del navegador para permitir que `localhost:4200` reciba datos de `localhost:8090`; se habilita con `@CrossOrigin`. |
| **Cascada** | Al borrar un padre (dueño), JPA borra hijos (mascotas → tratamientos) según `cascade`/`@OnDelete(CASCADE)`. |
| **Path param vs query param** | `/mascota/5` (identifica el recurso) vs `?veterinarioId=3` (contexto/filtro). |
| **Signal** | Valor reactivo de Angular: al `.set()`, Angular actualiza solo las vistas que lo leen. En template se lee como `miSignal()`. |
| **`combineLatest`** | RxJS: espera N observables y emite cuando todos respondieron, con un objeto `{clave: valor}`. |
| **`mergeMap`** | RxJS: encadena un HTTP que depende del anterior, "aplanning" el Observable interno. |
| **`of(x)`** | RxJS: observable que emite `x` inmediatamente; sirve de relleno en ramas condicionales. |
| **Signal input/output** | `input()` / `output()` de Angular 19: reemplazan `@Input`/`@Output`/`EventEmitter`. |
| **Formulario reactivo** | `FormGroup` + `FormControl` + `Validators`: modelo de formularios tipado y validable del lado cliente. |
