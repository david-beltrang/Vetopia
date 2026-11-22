import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing/landing-page.component';
import { MascotasComponent } from './pages/mascotas/mascotas.component';
import { FichaClinicaComponent } from './pages/ficha-clinica/ficha-clinica.component';
import { EditarMascotaComponent } from './pages/editar-mascota/editar-mascota.component';
import { RegistrarMascotaComponent } from './pages/registrar-mascota/registrar-mascota.component';
import { ListarClienteComponent } from './pages/listar-cliente/listar-cliente.component';
import { DetalleClienteComponent } from './pages/detalle-cliente/detalle-cliente.component';
import { RegistrarClienteComponent } from './pages/registrar-cliente/registrar-cliente.component';
import { EditarClienteComponent } from './pages/editar-cliente/editar-cliente.component';
import { LoginPageComponent } from './pages/login/login-page.component';
import { AsignarTratamientoComponent } from './pages/asignar-tratamiento/asignar-tratamiento.component';
import { TratamientoConfirmacionComponent } from './pages/tratamiento-confirmacion/tratamiento-confirmacion.component';
import { DetalleMascotaComponent } from './pages/detalle-mascota/detalle-mascota.component';
import { ListarMascotaComponent } from './pages/listar-mascota/listar-mascota.component';
import { ErrorPageComponent } from './pages/error/error-page.component';
import { TratamientosComponent } from './pages/tratamientos/tratamientos.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { DetallesVeterinarioComponent } from './pages/detalles-veterinario/detalles-veterinario.component';
import { ListarVeterinariosComponent } from './pages/listar-veterinarios/listar-veterinarios.component';
import { RegistrarVeterinarioComponent } from './pages/registrar-veterinario/registrar-veterinario.component';
import { CitasClienteComponent } from './pages/citas-cliente/citas-cliente.component';
import { CitasVeterinarioComponent } from './pages/citas-veterinario/citas-veterinario.component';
import { CitasAdminComponent } from './pages/citas-admin/citas-admin.component';
import { authGuard } from './guards/auth.guard';
import { rolGuard } from './guards/rol.guard';

export const routes: Routes = [
  // Rutas públicas
  { path: '', component: LandingPageComponent },
  { path: 'login', component: LoginPageComponent, title: 'Vetopia - Iniciar Sesión' },
  { path: 'error', component: ErrorPageComponent },

  // Rutas del veterinario
  {
    path: 'veterinario/mascotas',
    component: MascotasComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/tratamientos',
    component: TratamientosComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/citas',
    component: CitasVeterinarioComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/registrar-cliente',
    component: RegistrarClienteComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/registrar-mascota/:duenoId',
    component: RegistrarMascotaComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/ficha/:id',
    component: FichaClinicaComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/editar/:id',
    component: EditarMascotaComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/asignar-tratamiento/:id',
    component: AsignarTratamientoComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/mascotas/tratamiento-confirmacion/:mascotaId',
    component: TratamientoConfirmacionComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/clientes',
    component: ListarClienteComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/clientes/detalle/:id',
    component: DetalleClienteComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },
  {
    path: 'veterinario/clientes/editar/:id',
    component: EditarClienteComponent,
    canActivate: [authGuard, rolGuard(['VETERINARIO'])]
  },

  // Rutas del administrador
  {
    path: 'admin/dashboard',
    component: DashboardComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['ADMINISTRADOR'])]
  },
  {
    path: 'admin/citas',
    component: CitasAdminComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['ADMINISTRADOR'])]
  },
  {
    path: 'admin/veterinarios',
    component: ListarVeterinariosComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['ADMINISTRADOR'])]
  },
  {
    path: 'admin/registrar-veterinario',
    component: RegistrarVeterinarioComponent,
    canActivate: [authGuard, rolGuard(['ADMINISTRADOR'])]
  },
  {
    path: 'admin/detalles-veterinario/:id',
    component: DetallesVeterinarioComponent,
    canActivate: [authGuard, rolGuard(['ADMINISTRADOR'])]
  },

  // Rutas del cliente
  {
    path: 'cliente/mascotas/detalle',
    component: DetalleMascotaComponent,
    canActivate: [authGuard, rolGuard(['CLIENTE'])]
  },
  {
    path: 'cliente/mascotas',
    component: ListarMascotaComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['CLIENTE'])]
  },
  {
    path: 'cliente/citas',
    component: CitasClienteComponent,
    pathMatch: 'full',
    canActivate: [authGuard, rolGuard(['CLIENTE'])]
  },

  { path: '**', redirectTo: 'error' }
];
