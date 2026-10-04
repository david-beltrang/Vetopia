import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'app-datos-propietario',
  templateUrl: './datos-propietario.component.html'
})
export class DatosPropietarioComponent {
  propietario = input<Dueno | null>(null);
  veterinarioId = input<number>();
  router = inject(Router);

  navigateToPerfil(): void {
    if (this.propietario()) {
      this.router.navigate(['/veterinario/clientes/detalle'], {
        queryParams: { veterinarioId: this.veterinarioId(), id: this.propietario()!.id }
      });
    } else {
      this.router.navigate(['/error']);
    }
  }
}
