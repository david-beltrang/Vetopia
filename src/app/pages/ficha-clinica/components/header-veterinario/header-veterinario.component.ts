import { Component, input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Veterinario } from '../../../../models/veterinario.model';

@Component({
  selector: 'app-header-veterinario',
  imports: [CommonModule],
  templateUrl: './header-veterinario.component.html'
})
export class HeaderVeterinarioComponent {
  veterinario = input<Veterinario>();
  router = inject(Router);

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
