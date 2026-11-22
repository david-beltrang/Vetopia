import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { HeaderComponent } from '../../utils/header/header.component';
import { FooterComponent } from '../../utils/footer/footer.component';
import { HeroComponent } from './components/hero/hero.component';
import { ServicesComponent } from './components/services/services.component';
import { TeamComponent } from './components/team/team.component';
import { HowItWorksComponent } from './components/how-it-works/how-it-works.component';
import { LocationComponent } from './components/location/location.component';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-landing-page',
  imports: [
    HeaderComponent,
    FooterComponent,
    HeroComponent,
    ServicesComponent,
    TeamComponent,
    HowItWorksComponent,
    LocationComponent
  ],
  templateUrl: './landing-page.component.html'
})
export class LandingPageComponent implements OnInit {
  private auth   = inject(AuthService);
  private router = inject(Router);

  ngOnInit(): void {
    // Si no hay token, muestra la landing normal.
    if (!this.auth.estaAutenticado()) return;

    // El token puede quedar guardado de una sesión anterior (o haber expirado),
    // así que se valida contra el backend antes de redirigir. Si no sirve, se
    // limpia la sesión y la landing sigue visible en vez de mandar a /error.
    this.auth.validarSesion().subscribe((valido) => {
      if (!valido) {
        this.auth.logout();
        return;
      }

      const rol = this.auth.getRol();
      const id  = this.auth.getId();

      if (rol === 'VETERINARIO') {
        this.router.navigate(['/veterinario/mascotas'], { queryParams: { veterinarioId: id } });
      } else if (rol === 'CLIENTE') {
        this.router.navigate(['/cliente/mascotas'], { queryParams: { duenoId: id } });
      } else if (rol === 'ADMINISTRADOR') {
        this.router.navigate(['/admin/dashboard'], { queryParams: { adminId: id } });
      }
    });
  }
}
