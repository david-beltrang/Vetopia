import { Component, input, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'app-datos-propietario',
  templateUrl: './datos-propietario.component.html'
})
export class DatosPropietarioComponent {
  propietario = input<Dueno>();
  router = inject(Router);
  private route = inject(ActivatedRoute);

  private veterinarioId(): number {
    return Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;
  }

  navigateToPerfil(): void {
    const propietario = this.propietario();
    if (propietario?.id != null) {
      this.router.navigate(['/veterinario/clientes/detalle', propietario.id], {
        queryParams: { veterinarioId: this.veterinarioId() }
      });
    } else {
      this.router.navigate(['/error']);
    }
  }
}
