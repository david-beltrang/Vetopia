import { Component, input, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-datos-mascota',
  imports: [CommonModule],
  templateUrl: './datos-mascota.component.html'
})
export class DatosMascotaComponent {
  mascota = input<Mascota>();
  veterinarioId = input<number>();
  router = inject(Router);

  get tieneImagen(): boolean {
    return !!this.mascota()?.imagen;
  }

  navigateToEditar(): void {
    this.router.navigate(['/veterinario/mascotas/editar'], {
      queryParams: { id: this.mascota()?.id }
    });
  }

  navigateToAsignarTratamiento(): void {
    this.router.navigate(['/veterinario/mascotas/asignar-tratamiento'], {
      queryParams: { veterinarioId: this.veterinarioId(), id: this.mascota()?.id }
    });
  }
}
