import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, map, mergeMap, of } from 'rxjs';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { TratamientoDetalle } from '../../models/ficha-clinica.model';
import { HeaderVeterinarioComponent } from './components/header-veterinario/header-veterinario.component';
import { EstadoPacienteComponent } from './components/estado-paciente/estado-paciente.component';
import { DatosMascotaComponent } from './components/datos-mascota/datos-mascota.component';
import { DatosPropietarioComponent } from './components/datos-propietario/datos-propietario.component';
import { HistorialMedicoComponent } from './components/historial-medico/historial-medico.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';

@Component({
  selector: 'app-ficha-clinica',
  imports: [
    HeaderVeterinarioComponent,
    EstadoPacienteComponent,
    DatosMascotaComponent,
    DatosPropietarioComponent,
    HistorialMedicoComponent,
    FooterPortalComponent
  ],
  templateUrl: './ficha-clinica.component.html'
})
export class FichaClinicaComponent implements OnInit {
  mascotaSeleccionada?: Mascota;
  veterinario?: Veterinario;
  propietarioSeleccionado?: Dueno;
  historialSeleccionado: TratamientoDetalle[] = [];

  veterinarioId = 0;
  cargando: boolean = true;
  mensajeError = '';

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);
  private tratamientoService = inject(TratamientoService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    const idParam = this.route.snapshot.params['id'];
    if (idParam) {
      this.cargarFicha(Number(idParam));
    } else {
      this.cargando = false;
      this.mensajeError = 'Parámetros inválidos';
    }
  }

  private cargarFicha(id: number): void {
    // Espera los datos de la mascota y veterinario antes de cargar el historial.
    // El dueño ya no viene dentro de la mascota, así que se consulta por su id.
    combineLatest({
      mascota: this.mascotaService.obtenerMascotaPorId(id),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).pipe(
      mergeMap(({ mascota, veterinario }) =>
        combineLatest({
          historial: this.tratamientoService.listarHistorialDeMascota(id),
          propietario: mascota.duenoId != null
            ? this.duenoService.obtenerDuenoPorId(mascota.duenoId)
            : of<Dueno | undefined>(undefined)
        }).pipe(
          map(({ historial, propietario }) => ({ mascota, veterinario, historial, propietario }))
        )
      )
    ).subscribe({
      next: ({ mascota, veterinario, historial, propietario }) => {
        this.mascotaSeleccionada = mascota;
        this.veterinario = veterinario;
        this.propietarioSeleccionado = propietario;
        this.historialSeleccionado = historial;
        this.cargando = false;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  onEstadoCambiado(mascota: Mascota): void {
    if (mascota.id == null) return;

    this.mascotaService.cambiarEstado(mascota.id, mascota.estado).subscribe({
      next: () => { this.mascotaSeleccionada = mascota; },
      error: () => this.router.navigate(['/error'])
    });
  }

  navigateToMascotas(): void {
    this.router.navigate(['/veterinario/mascotas'], { queryParams: { veterinarioId: this.veterinarioId } });
  }
}
