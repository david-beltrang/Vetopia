import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TratamientoService } from '../../service/tratamiento.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { Veterinario } from '../../models/veterinario.model';
import { TratamientoDetalle } from '../../models/ficha-clinica.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { CambioEstadoTratamiento, ItemTratamientoVeterinarioComponent } from './components/item-tratamiento-veterinario/item-tratamiento-veterinario.component';

@Component({
  selector: 'app-tratamientos',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    ItemTratamientoVeterinarioComponent
  ],
  templateUrl: './tratamientos.component.html'
})
export class TratamientosComponent implements OnInit {
  veterinario?: Veterinario;
  tratamientos: TratamientoDetalle[] = [];
  veterinarioId = 0;

  // Estado del cambio Completado/Pendiente: qué fila guarda y qué error mostrar.
  guardandoId = signal<number | null>(null);
  erroresEstado = signal<Record<number, string>>({});

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private veterinarioService = inject(VeterinarioService);
  private tratamientoService = inject(TratamientoService);

  ngOnInit(): void {
    // El veterinario viaja en la URL para conservar la sesión al recargar.
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: (veterinario) => { this.veterinario = veterinario; },
      error: () => this.router.navigate(['/error'])
    });

    // Sin id no hay historial que pedir.
    if (this.veterinarioId > 0) {
      this.tratamientoService.listarHistorialDeVeterinario(this.veterinarioId).subscribe({
        next: (tratamientos) => { this.tratamientos = tratamientos; },
        error: () => this.router.navigate(['/error'])
      });
    }
  }

  onEstadoCambiado(evento: CambioEstadoTratamiento): void {
    this.guardandoId.set(evento.tratamientoId);
    this.limpiarError(evento.tratamientoId);

    this.tratamientoService.actualizarEstado(evento.tratamientoId, evento.completado).subscribe({
      next: (tratamiento) => {
        // Se actualiza la entidad del historial para que el badge cambie.
        const item = this.tratamientos.find(t => t.tratamiento.id === evento.tratamientoId);
        if (item) {
          item.tratamiento.completado = tratamiento.completado;
        }
        this.guardandoId.set(null);
      },
      error: (err) => {
        this.erroresEstado.update(errores => ({
          ...errores,
          [evento.tratamientoId]: err?.error?.message || 'No se pudo actualizar el estado del tratamiento.'
        }));
        this.guardandoId.set(null);
      }
    });
  }

  mensajeErrorDe(id: number | undefined): string {
    return id == null ? '' : this.erroresEstado()[id] ?? '';
  }

  private limpiarError(id: number): void {
    this.erroresEstado.update(errores => {
      if (!(id in errores)) return errores;
      const copia = { ...errores };
      delete copia[id];
      return copia;
    });
  }
}
