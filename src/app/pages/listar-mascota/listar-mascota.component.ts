import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
import { HeaderClienteComponent } from '../../utils/header-cliente/header-cliente.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoMascotasComponent } from './components/encabezado-mascotas/encabezado-mascotas.component';
import { TablaMascotasComponent } from './components/tabla-mascotas/tabla-mascotas.component';

@Component({
  selector: 'app-listar-mascota',
  imports: [
    HeaderClienteComponent,
    FooterPortalComponent,
    EncabezadoMascotasComponent,
    TablaMascotasComponent
  ],
  templateUrl: './listar-mascota.component.html'
})
export class ListarMascotaComponent implements OnInit {
  duenoId = 0;
  mascotas: Mascota[] = [];

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    this.duenoId = Number(this.route.snapshot.queryParamMap.get('duenoId')) || 0;

    // Si el cliente está identificado se filtran sus mascotas; si no, se listan todas.
    const peticion = this.duenoId > 0
      ? this.mascotaService.listarMascotasDeDueno(this.duenoId)
      : this.mascotaService.listarMascotas();

    peticion.subscribe({
      next: (mascotas) => { this.mascotas = mascotas; },
      error: () => this.router.navigate(['/error'])
    });
  }

  get textoContador(): string {
    const total = this.mascotas.length;
    return total === 1 ? '1 mascota registrada' : `${total} mascotas registradas`;
  }

  verDetalle(mascota: Mascota): void {
    this.router.navigate(['/cliente/mascotas/detalle'], {
      queryParams: { duenoId: this.duenoId, id: mascota.id }
    });
  }
}
