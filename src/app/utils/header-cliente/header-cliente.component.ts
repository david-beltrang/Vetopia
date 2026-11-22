import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-header-cliente',
  templateUrl: './header-cliente.component.html'
})
export class HeaderClienteComponent {
  duenoId = input(0);
  seccionActiva = input<'mascotas' | 'citas'>('mascotas');
  private router = inject(Router);
  private auth   = inject(AuthService);

  irInicio(): void {
    this.router.navigate(['/']);
  }

  irMascotas(): void {
    this.router.navigate(['/cliente/mascotas'], { queryParams: { duenoId: this.duenoId() } });
  }

  irCitas(): void {
    this.router.navigate(['/cliente/citas'], { queryParams: { duenoId: this.duenoId() } });
  }

  cerrarSesion(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
