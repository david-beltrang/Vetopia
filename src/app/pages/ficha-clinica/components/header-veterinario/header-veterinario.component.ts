import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Veterinario } from '../../../../models/veterinario.model';

export type SeccionVeterinario = 'pacientes' | 'clientes' | 'tratamientos';

@Component({
  selector: 'app-header-veterinario',
  templateUrl: './header-veterinario.component.html'
})
export class HeaderVeterinarioComponent {
  veterinario = input<Veterinario>();
  veterinarioId = input<number>(0);
  seccionActiva = input<SeccionVeterinario>('pacientes');
  router = inject(Router);

  iniciales(): string {
    const nombre = this.veterinario()?.nombre;
    return nombre ? nombre.slice(0, 2).toUpperCase() : 'VT';
  }

  esActiva(seccion: SeccionVeterinario): boolean {
    return this.seccionActiva() === seccion;
  }

  claseEnlace(seccion: SeccionVeterinario): string {
    const base = 'font-body-md text-body-md transition-colors duration-200';
    return this.esActiva(seccion)
      ? `${base} text-[#7fffd4] font-bold border-b-2 border-[#7fffd4] pb-1 shadow-[0px_2px_4px_rgba(0,0,0,0.30)] hover:text-primary`
      : `${base} text-gray-300 hover:text-primary`;
  }

  navigateToMascotas(): void {
    this.router.navigate(['/veterinario/mascotas'], { queryParams: { veterinarioId: this.veterinarioId() } });
  }

  navigateToClientes(): void {
    this.router.navigate(['/veterinario/clientes'], { queryParams: { veterinarioId: this.veterinarioId() } });
  }

  navigateToTratamientos(): void {
    this.router.navigate(['/veterinario/mascotas/tratamientos'], { queryParams: { veterinarioId: this.veterinarioId() } });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
