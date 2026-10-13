import { Component, input, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-datos-mascota',
  templateUrl: './datos-mascota.component.html'
})
export class DatosMascotaComponent {
  mascota = input<Mascota>();
  router = inject(Router);
  private route = inject(ActivatedRoute);

  get tieneImagen(): boolean {
    return !!this.mascota()?.imagen;
  }

  private veterinarioId(): number {
    return Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;
  }

  navigateToEditar(): void {
    const id = this.mascota()?.id;
    if (id) {
      this.router.navigate(['/veterinario/mascotas/editar', id], {
        queryParams: { veterinarioId: this.veterinarioId() }
      });
    }
  }

  navigateToAsignarTratamiento(): void {
    const id = this.mascota()?.id;
    if (id) {
      this.router.navigate(['/veterinario/mascotas/asignar-tratamiento', id], {
        queryParams: { veterinarioId: this.veterinarioId() }
      });
    }
  }
}
