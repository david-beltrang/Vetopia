import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoMascotasComponent } from './components/encabezado-mascotas/encabezado-mascotas.component';
import { TablaMascotasComponent } from './components/tabla-mascotas/tabla-mascotas.component';

@Component({
  selector: 'app-mascotas',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    EncabezadoMascotasComponent,
    TablaMascotasComponent
  ],
  templateUrl: './mascotas.component.html'
})
export class MascotasComponent implements OnInit {
  mascotas: Mascota[] = [];
  veterinario?: Veterinario;
  veterinarioId = 0;
  // Mapa id de dueño -> nombre, para mostrar la columna "Dueño" sin que el
  // dueño venga duplicado dentro de cada mascota.
  duenosPorId = new Map<number, string>();

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    // Espera las respuestas de las peticiones independientes.
    combineLatest({
      mascotas: this.mascotaService.listarMascotas(),
      duenos: this.duenoService.listarDuenos(),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).subscribe({
      next: ({ mascotas, duenos, veterinario }) => {
        this.mascotas = mascotas;
        this.veterinario = veterinario;
        const mapaDuenos = new Map<number, string>();
        for (const dueno of duenos) {
          if (dueno.id != null) {
            mapaDuenos.set(dueno.id, dueno.nombre);
          }
        }
        this.duenosPorId = mapaDuenos;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  get totalMascotas(): number {
    return this.mascotas.length;
  }

  get textoContador(): string {
    return this.totalMascotas === 1 ? '1 mascota registrada' : `${this.totalMascotas} mascotas registradas`;
  }

  verFicha(mascota: Mascota): void {
    this.router.navigate(['/veterinario/mascotas/ficha', mascota.id], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }
}
