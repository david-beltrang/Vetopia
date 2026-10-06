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

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'veterinario/mascotas', component: MascotasComponent, pathMatch: 'full' },
  { path: 'veterinario/mascotas/crear', component: RegistrarMascotaComponent },
  { path: 'veterinario/mascotas/registrar-cliente', component: RegistrarClienteComponent },
  { path: 'veterinario/mascotas/registrar-mascota/:duenoId', component: RegistrarMascotaComponent },
  { path: 'veterinario/mascotas/ficha/:id', component: FichaClinicaComponent },
  { path: 'veterinario/mascotas/editar/:id', component: EditarMascotaComponent },
  { path: 'veterinario/clientes', component: ListarClienteComponent, pathMatch: 'full' },
  { path: 'veterinario/clientes/detalle/:id', component: DetalleClienteComponent },
  { path: 'veterinario/clientes/editar/:id', component: EditarClienteComponent },
  { path: '**', redirectTo: 'veterinario/mascotas' }
];
