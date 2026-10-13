import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MascotaService } from '../../service/mascota.service';
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

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    forkJoin({
      mascotas: this.mascotaService.listarMascotas(),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).subscribe({
      next: ({ mascotas, veterinario }) => {
        this.mascotas = mascotas;
        this.veterinario = veterinario;
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
