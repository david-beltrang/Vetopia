import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { map, mergeMap } from 'rxjs';
import { MascotaService } from '../../service/mascota.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { Mascota } from '../../models/mascota.model';
import { TratamientoDetalle } from '../../models/ficha-clinica.model';
import { HeaderClienteComponent } from '../../utils/header-cliente/header-cliente.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { PanelFotoMascotaComponent } from './components/panel-foto-mascota/panel-foto-mascota.component';
import { DatosMascotaClienteComponent } from './components/datos-mascota-cliente/datos-mascota-cliente.component';
import { HistorialMascotaComponent } from './components/historial-mascota/historial-mascota.component';
import { MascotaNoEncontradaComponent } from './components/mascota-no-encontrada/mascota-no-encontrada.component';

@Component({
  selector: 'app-detalle-mascota',
  imports: [
    HeaderClienteComponent,
    FooterPortalComponent,
    PanelFotoMascotaComponent,
    DatosMascotaClienteComponent,
    HistorialMascotaComponent,
    MascotaNoEncontradaComponent
  ],
  templateUrl: './detalle-mascota.component.html'
})
export class DetalleMascotaComponent implements OnInit {
  duenoId = 0;
  mascota?: Mascota;
  historial: TratamientoDetalle[] = [];
  mensajeError = 'No encontramos ninguna mascota registrada.';
  cargando = true;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private tratamientoService = inject(TratamientoService);

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    this.duenoId = Number(params.get('duenoId')) || 0;

    const idParam = params.get('id');
    if (!idParam) {
      this.mensajeError = 'Parámetros inválidos';
      this.cargando = false;
      return;
    }

    this.cargarMascota(Number(idParam));
  }

  private cargarMascota(id: number): void {
    this.mascotaService.obtenerMascotaPorId(id).pipe(
      mergeMap(mascota =>
        this.tratamientoService.listarHistorialDeMascota(id).pipe(
          map(historial => ({ mascota, historial }))
        )
      )
    ).subscribe({
      next: ({ mascota, historial }) => {
        this.mascota = mascota;
        this.historial = historial;
        this.cargando = false;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  volverMascotas(): void {
    this.router.navigate(['/cliente/mascotas'], { queryParams: { duenoId: this.duenoId } });
  }
}
