import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
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
  unidadesDescontadas = 2;
  medicamentoNombre = 'Medicamento';
  stockRestante = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    this.veterinario = this.mascotaService.getVeterinarios()[0];

    const idParam = this.route.snapshot.params['mascotaId'];
    if (idParam) {
      this.mascota = this.mascotaService.getMascotaById(Number(idParam));
    }

    // Datos de la confirmación enviados por la pantalla de asignación.
    const state = (history.state ?? {}) as {
      unidadesDescontadas?: number;
      medicamentoNombre?: string;
      stockRestante?: number;
    };

    if (typeof state.unidadesDescontadas === 'number') {
      this.unidadesDescontadas = state.unidadesDescontadas;
    }
    if (state.medicamentoNombre) {
      this.medicamentoNombre = state.medicamentoNombre;
    }
    if (typeof state.stockRestante === 'number') {
      this.stockRestante = state.stockRestante;
    }
  }

  volverFicha(): void {
    const mascota = this.mascota;
    if (mascota) {
      this.router.navigate(['/veterinario/mascotas/ficha', mascota.id]);
    } else {
      this.router.navigate(['/veterinario/mascotas']);
    }
  }

  cerrar(): void {
    this.router.navigate(['/veterinario/mascotas']);
  }
}
