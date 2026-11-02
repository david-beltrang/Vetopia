import { Component, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';
import { VeterinarioService } from '../../service/veterinario.service';
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
  private authService = inject(AuthService);
  private veterinarioService = inject(VeterinarioService);

  onIngresar(credenciales: { correo: string; contrasena: string }): void {
    this.authService.login({
      correo: credenciales.correo.trim(),
      contrasena: credenciales.contrasena
    }).subscribe({
      next: (resultado) => {
        this.mensajeError.set('');
        this.ingresarSegunRol(resultado.rol, resultado.id);
      },
      error: (err) => this.mensajeError.set(err?.error?.message || 'Credenciales incorrectas.')
    });
  }

  private ingresarSegunRol(rol: string, id: number): void {
    if (rol === 'CLIENTE') {
      this.router.navigate(['/cliente/mascotas'], { queryParams: { duenoId: id } });
      return;
    }

    if (rol === 'VETERINARIO') {
      this.router.navigate(['/veterinario/mascotas'], { queryParams: { veterinarioId: id } });
      return;
    }

    if (rol === 'ADMINISTRADOR') {
      this.router.navigate(['/admin/dashboard'], { queryParams: { adminId: id } });
      return;
    }

    this.mensajeError.set('Rol no reconocido.');
  }
}
