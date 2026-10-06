import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-datos-mascota',
  templateUrl: './datos-mascota.component.html'
})
export class DatosMascotaComponent {
  mascota = input<Mascota>();
  router = inject(Router);

  get tieneImagen(): boolean {
    return !!this.mascota()?.imagen;
  }

  navigateToEditar(): void {
    const id = this.mascota()?.id;
    if (id) {
      this.router.navigate(['/veterinario/mascotas/editar', id]);
    }
  }

  navigateToAsignarTratamiento(): void {
    this.router.navigate(['/veterinario/mascotas/asignar-tratamiento']);
  }
}
