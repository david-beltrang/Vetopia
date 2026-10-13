import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { HeroLoginComponent } from './components/hero-login/hero-login.component';
import { FormularioLoginComponent } from './components/formulario-login/formulario-login.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';

@Component({
  selector: 'app-login-page',
  imports: [
    HeroLoginComponent,
    FormularioLoginComponent,
    FooterPortalComponent
  ],
  templateUrl: './login-page.component.html'
})
export class LoginPageComponent {
  mensajeError = signal('');

  private router = inject(Router);
  private duenoService = inject(DuenoService);
  private mascotaService = inject(MascotaService);

  onIngresar(credenciales: { username: string; password: string }): void {
    const { username, password } = credenciales;
    const usuario = username.trim().toLowerCase();

    const esDuenoValido = this.duenoService.listarDuenos().some((dueno) =>
      (dueno.cedula === username || dueno.correo.toLowerCase() === usuario) && dueno.contrasena === password
    );

    const esVeterinarioValido = this.mascotaService.getVeterinarios().some((veterinario) =>
      (veterinario.cedula === username || veterinario.correo.toLowerCase() === usuario) && veterinario.contrasena === password
    );

    if (esDuenoValido || esVeterinarioValido) {
      this.mensajeError.set('');
      this.router.navigate(['/veterinario/mascotas']);
    } else {
      this.mensajeError.set('Credenciales incorrectas.');
    }
  }
}
