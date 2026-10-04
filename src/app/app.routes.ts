import { Routes } from '@angular/router';
import { LandingPageComponent } from './pages/landing/landing-page.component';
import { MascotasComponent } from './pages/mascotas/mascotas.component';
import { FichaClinicaComponent } from './pages/ficha-clinica/ficha-clinica.component';
import { EditarMascotaComponent } from './pages/editar-mascota/editar-mascota.component';
import { RegistrarMascotaComponent } from './pages/registrar-mascota/registrar-mascota.component';

export const routes: Routes = [
  { path: '', component: LandingPageComponent },
  { path: 'veterinario/mascotas', component: MascotasComponent },
  { path: 'veterinario/mascotas/ficha', component: FichaClinicaComponent },
  { path: 'veterinario/mascotas/editar', component: EditarMascotaComponent },
  { path: 'veterinario/mascotas/crear', component: RegistrarMascotaComponent }
];
