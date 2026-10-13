import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { MascotaService } from '../../service/mascota.service';
import { DrogaService } from '../../service/droga.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { TratamientoService, TratamientoAsignacion } from '../../service/tratamiento.service';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { Droga } from '../../models/droga.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoTratamientoComponent } from './components/encabezado-tratamiento/encabezado-tratamiento.component';
import { FormularioTratamientoComponent, TratamientoFormValue } from './components/formulario-tratamiento/formulario-tratamiento.component';

@Component({
  selector: 'app-asignar-tratamiento',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    EncabezadoTratamientoComponent,
    FormularioTratamientoComponent
  ],
  templateUrl: './asignar-tratamiento.component.html'
})
export class AsignarTratamientoComponent implements OnInit {
  mascota?: Mascota;
  dueno?: Dueno;
  veterinario?: Veterinario;
  drogas: Droga[] = [];
  mensajeError = signal('');
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private drogaService = inject(DrogaService);
  private veterinarioService = inject(VeterinarioService);
  private tratamientoService = inject(TratamientoService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    const idParam = this.route.snapshot.params['id'];
    if (!idParam) {
      this.mensajeError.set('Parámetros inválidos');
      return;
    }

    forkJoin({
      mascota: this.mascotaService.obtenerMascotaPorId(Number(idParam)),
      drogas: this.drogaService.listarDrogas(),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).subscribe({
      next: ({ mascota, drogas, veterinario }) => {
        this.mascota = mascota;
        this.dueno = mascota.dueno;
        this.drogas = drogas;
        this.veterinario = veterinario;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  onGuardar(valor: TratamientoFormValue): void {
    const mascota = this.mascota;
    if (!mascota?.id) return;

    // El backend solo necesita el id de la mascota y de la droga en el cuerpo.
    const tratamiento: TratamientoAsignacion = {
      fecha: valor.fecha,
      mascota: { id: mascota.id },
      droga: { id: valor.drogaId },
      titulo: valor.titulo,
      descripcion: valor.descripcion,
      completado: false
    };

    this.tratamientoService.asignarTratamiento(tratamiento, this.veterinarioId).subscribe({
      next: (resultado) => {
        this.router.navigate(['/veterinario/mascotas/tratamiento-confirmacion', mascota.id], {
          queryParams: { veterinarioId: this.veterinarioId },
          state: { resultado }
        });
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  onCancelar(): void {
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
}
