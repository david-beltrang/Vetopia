import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { ResultadoTratamientoDTO } from '../../models/resultado-tratamiento.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { ModalConfirmacionComponent } from './components/modal-confirmacion/modal-confirmacion.component';

@Component({
  selector: 'app-tratamiento-confirmacion',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    ModalConfirmacionComponent
  ],
  templateUrl: './tratamiento-confirmacion.component.html'
})
export class TratamientoConfirmacionComponent implements OnInit {
  mascota?: Mascota;
  veterinario?: Veterinario;
  unidadesDescontadas = 0;
  medicamentoNombre = 'Medicamento';
  stockRestante = 0;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: (veterinario) => { this.veterinario = veterinario; },
      error: () => this.router.navigate(['/error'])
    });

    const idParam = this.route.snapshot.params['mascotaId'];
    if (idParam) {
      this.mascotaService.obtenerMascotaPorId(Number(idParam)).subscribe({
        next: (mascota) => { this.mascota = mascota; },
        error: () => this.router.navigate(['/error'])
      });
    }

    // Datos de la confirmación enviados por la pantalla de asignación.
    const state = (history.state ?? {}) as { resultado?: ResultadoTratamientoDTO };
    const resultado = state.resultado;
    if (resultado) {
      this.unidadesDescontadas = resultado.unidadesDescontadas;
      this.stockRestante = resultado.stockRestante;
      if (resultado.medicamentoNombre) {
        this.medicamentoNombre = resultado.medicamentoNombre;
      }
      if (resultado.mascota) {
        this.mascota = resultado.mascota;
      }
    }
  }

  volverFicha(): void {
    const mascota = this.mascota;
    if (mascota?.id) {
      this.router.navigate(['/veterinario/mascotas/ficha', mascota.id], {
        queryParams: { veterinarioId: this.veterinarioId }
      });
    } else {
      this.router.navigate(['/veterinario/mascotas'], {
        queryParams: { veterinarioId: this.veterinarioId }
      });
    }
  }

  cerrar(): void {
    this.router.navigate(['/veterinario/mascotas'], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }
}
