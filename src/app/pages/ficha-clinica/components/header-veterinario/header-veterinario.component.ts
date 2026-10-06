import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Veterinario } from '../../../../models/veterinario.model';

@Component({
  selector: 'app-header-veterinario',
  templateUrl: './header-veterinario.component.html'
})
export class HeaderVeterinarioComponent {
  veterinario = input<Veterinario>();
  seccionActiva = input<'pacientes' | 'clientes'>('pacientes');
  router = inject(Router);

  iniciales(): string {
    const nombre = this.veterinario()?.nombre;
    return nombre ? nombre.slice(0, 2).toUpperCase() : 'VT';
  }

  navigateToMascotas(): void {
    this.router.navigate(['/veterinario/mascotas']);
  }

  navigateToClientes(): void {
    this.router.navigate(['/veterinario/clientes']);
  }

  navigateToTratamientos(): void {
    this.router.navigate(['/veterinario/mascotas/tratamientos']);
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
