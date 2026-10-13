import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
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
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    this.mascotas = this.mascotaService.getMascotas();
  }

  get totalMascotas(): number {
    return this.mascotas.length;
  }

  get textoContador(): string {
    return this.totalMascotas === 1 ? '1 mascota registrada' : `${this.totalMascotas} mascotas registradas`;
  }

  verFicha(mascota: Mascota): void {
    this.router.navigate(['/veterinario/mascotas/ficha', mascota.id]);
  }
}
