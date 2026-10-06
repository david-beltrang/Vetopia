import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'app-datos-propietario',
  templateUrl: './datos-propietario.component.html'
})
export class DatosPropietarioComponent {
  propietario = input<Dueno>();
  router = inject(Router);

  navigateToPerfil(): void {
    const propietario = this.propietario();
    if (propietario) {
      this.router.navigate(['/veterinario/clientes/detalle', propietario.id]);
    } else {
      this.router.navigate(['/error']);
    }
  }
}
