import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { TratamientoDetalle } from '../../models/ficha-clinica.model';
import { Tratamiento } from '../../models/tratamiento.model';
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

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  private tratamientosBase: TratamientoDetalle[] = [
    {
      tratamiento: {
        id: 1,
        fecha: new Date('2026-01-15'),
        veterinario: { id: 1, cedula: '2012345678', contrasena: 'vet123', correo: 'laura@vetopia.com', especialidad: 'Medicina Veterinaria', numeroAtenciones: 150, nombre: 'Dra. Laura Gómez', estado: 'Activo', urlFoto: '', tratamientos: [] },
        droga: { id: 1, nombre: 'Amoxicilina', precioCompra: 5000, precioVenta: 8000, unidadesDisponibles: 100, unidadesVendidas: 50, tratamientos: [] },
        titulo: 'Desparasitación',
        descripcion: 'Tratamiento antiparasitario completo',
        completado: true,
      },
      drogaNombre: 'Amoxicilina',
    },
    {
      tratamiento: {
        id: 2,
        fecha: new Date('2026-02-20'),
        veterinario: { id: 2, cedula: '2023456789', contrasena: 'vet123', correo: 'carlos@vetopia.com', especialidad: 'Veterinario Cirujano', numeroAtenciones: 200, nombre: 'Dr. Carlos Ruiz', estado: 'Activo', urlFoto: '', tratamientos: [] },
        droga: { id: 2, nombre: 'Ivermectina', precioCompra: 3000, precioVenta: 5000, unidadesDisponibles: 80, unidadesVendidas: 30, tratamientos: [] },
        titulo: 'Cirugía menor',
        descripcion: 'Extracción de cuerpo extraño',
        completado: true,
      },
      drogaNombre: 'Ivermectina',
    },
    {
      tratamiento: {
        id: 3,
        fecha: new Date('2026-03-10'),
        veterinario: { id: 3, cedula: '2034567890', contrasena: 'vet123', correo: 'felipe@vetopia.com', especialidad: 'Cardiología veterinaria', numeroAtenciones: 80, nombre: 'Dra. Felipe Torres', estado: 'Activo', urlFoto: '', tratamientos: [] },
        droga: { id: 3, nombre: 'Antiparasitario', precioCompra: 4000, precioVenta: 6500, unidadesDisponibles: 60, unidadesVendidas: 25, tratamientos: [] },
        titulo: 'Control cardíaco',
        descripcion: 'Revisión y medicación',
        completado: false,
      },
      drogaNombre: 'Antiparasitario',
    },
  ];

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    this.duenoId = Number(params.get('duenoId')) || 0;

    const idParam = params.get('id');
    if (!idParam) {
      this.mensajeError = 'Parámetros inválidos';
      return;
    }

    this.cargarMascota(Number(idParam));
  }

  private cargarMascota(id: number): void {
    const mascota = this.mascotaService.getMascotaById(id);
    if (mascota) {
      this.mascota = mascota;
      this.historial = this.historialDe(mascota);
    } else {
      this.mensajeError = 'No encontramos ninguna mascota registrada.';
    }
  }

  private historialDe(mascota: Mascota): TratamientoDetalle[] {
    return this.tratamientosBase.map((detalle) => {
      const tratamiento: Tratamiento = Object.assign({}, detalle.tratamiento, { mascota });
      return Object.assign({}, detalle, { tratamiento });
    });
  }

  volverMascotas(): void {
    this.router.navigate(['/cliente/mascotas'], { queryParams: { duenoId: this.duenoId } });
  }
}
