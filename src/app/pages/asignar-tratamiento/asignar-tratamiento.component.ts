import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { DrogaService } from '../../service/droga.service';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { Droga } from '../../models/droga.model';
import { Tratamiento } from '../../models/tratamiento.model';
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

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private drogaService = inject(DrogaService);

  ngOnInit(): void {
    this.drogas = this.drogaService.getDrogas();
    this.veterinario = this.mascotaService.getVeterinarios()[0];

    const idParam = this.route.snapshot.params['id'];
    if (!idParam) {
      this.mensajeError.set('Parámetros inválidos');
      return;
    }

    const mascota = this.mascotaService.getMascotaById(Number(idParam));
    if (!mascota) {
      this.mensajeError.set('No encontramos la mascota seleccionada.');
      return;
    }

    this.mascota = mascota;
    this.dueno = mascota.dueno;
  }

  onGuardar(valor: TratamientoFormValue): void {
    const mascota = this.mascota;
    const veterinario = this.veterinario;
    if (!mascota || !veterinario) return;

    const droga = this.drogaService.getDrogaById(valor.drogaId);
    if (!droga) {
      this.mensajeError.set('Selecciona un medicamento válido.');
      return;
    }

    const unidadesDescontadas = 2;
    droga.unidadesDisponibles = Math.max(0, droga.unidadesDisponibles - unidadesDescontadas);
    droga.unidadesVendidas += unidadesDescontadas;

    const tratamiento: Tratamiento = {
      id: 0,
      fecha: new Date(valor.fecha),
      mascota,
      veterinario,
      droga,
      titulo: valor.titulo,
      descripcion: valor.descripcion,
      completado: false
    };

    this.mascotaService.updateMascota(mascota.id, {
      ...mascota,
      tratamientos: [...mascota.tratamientos, tratamiento]
    });

    this.router.navigate(['/veterinario/mascotas/tratamiento-confirmacion', mascota.id], {
      state: {
        unidadesDescontadas,
        medicamentoNombre: droga.nombre,
        stockRestante: droga.unidadesDisponibles
      }
    });
  }

  onCancelar(): void {
    const mascota = this.mascota;
    if (mascota) {
      this.router.navigate(['/veterinario/mascotas/ficha', mascota.id]);
    } else {
      this.router.navigate(['/veterinario/mascotas']);
    }
  }
}
