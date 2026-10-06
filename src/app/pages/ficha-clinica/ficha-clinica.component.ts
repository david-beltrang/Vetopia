import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { TratamientoDetalle } from '../../models/ficha-clinica.model';
import { Tratamiento } from '../../models/tratamiento.model';
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
  mascotas: Mascota[] = [];
  mascotaSeleccionada?: Mascota;
  veterinario?: Veterinario;
  propietarioSeleccionado?: Dueno;
  historialSeleccionado: TratamientoDetalle[] = [];

  cargando: boolean = true;
  mensajeError = '';
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
    this.mascotas = this.mascotaService.getMascotas();

    const idParam = this.route.snapshot.params['id'];

    if (idParam) {
      this.cargarFicha(Number(idParam));
    } else {
      this.cargando = false;
      this.mensajeError = 'Parámetros inválidos';
    }
  }

  private cargarFicha(id: number): void {
    const mascota = this.mascotas.find(m => m.id === id);
    if (mascota) {
      this.mascotaSeleccionada = mascota;
      this.veterinario = this.mascotaService.getVeterinarios()[0];
      this.propietarioSeleccionado = mascota.dueno;
      this.historialSeleccionado = this.historialDe(mascota);
    } else {
      this.mensajeError = 'No encontramos ninguna mascota registrada.';
    }
    this.cargando = false;
  }

  private historialDe(mascota: Mascota): TratamientoDetalle[] {
    return this.tratamientosBase.map((detalle) => {
      const tratamiento: Tratamiento = Object.assign({}, detalle.tratamiento, { mascota });
      return Object.assign({}, detalle, { tratamiento });
    });
  }

  onEstadoCambiado(mascota: Mascota): void {
    const actualizada = this.mascotaService.updateMascota(mascota.id, mascota);
    if (actualizada) {
      this.mascotaSeleccionada = actualizada;
    }
  }

  navigateToMascotas(): void {
    this.router.navigate(['/veterinario/mascotas']);
  }
}
