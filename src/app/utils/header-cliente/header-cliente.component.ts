import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header-cliente',
  templateUrl: './header-cliente.component.html'
})
export class HeaderClienteComponent {
  duenoId = input(0);
  private router = inject(Router);

  irInicio(): void {
    this.router.navigate(['/']);
  }

  irMascotas(): void {
    this.router.navigate(['/cliente/mascotas'], { queryParams: { duenoId: this.duenoId() } });
  }

  cerrarSesion(): void {
    this.router.navigate(['/login']);
  }
}
