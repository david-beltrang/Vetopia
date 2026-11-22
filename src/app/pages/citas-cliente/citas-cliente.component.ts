import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { Cita, ClasificacionMotivoCita, CrearCita, HorarioDisponible } from '../../models/cita.model';
import { Mascota } from '../../models/mascota.model';
import { CitaService } from '../../service/cita.service';
import { MascotaService } from '../../service/mascota.service';
import { HeaderClienteComponent } from '../../utils/header-cliente/header-cliente.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { UBICACION_VETOPIA } from '../../config/ubicacion-vetopia';

@Component({
  selector: 'app-citas-cliente',
  imports: [CommonModule, FormsModule, HeaderClienteComponent, FooterPortalComponent],
  templateUrl: './citas-cliente.component.html'
})
export class CitasClienteComponent implements OnInit {
  private static readonly MAX_MASCOTAS_CITA = 5;
  private route = inject(ActivatedRoute);
  private mascotaService = inject(MascotaService);
  private citaService = inject(CitaService);
  private sanitizer = inject(DomSanitizer);

  duenoId = 0;
  mascotas: Mascota[] = [];
  citas: Cita[] = [];
  mascotasSeleccionadas = new Set<number>();
  motivo = '';
  fecha = this.fechaInicial();
  especialidades: string[] = [];
  especialidad = '';
  clasificacion?: ClasificacionMotivoCita;
  horarios: HorarioDisponible[] = [];
  horarioSeleccionado = '';
  citaConfirmada?: Cita;
  error = '';
  clasificando = false;
  cargandoHorarios = false;
  guardando = false;

  readonly ubicacion = UBICACION_VETOPIA;
  readonly mapaEmbedUrl: SafeResourceUrl = this.sanitizer
    .bypassSecurityTrustResourceUrl(UBICACION_VETOPIA.urlMapaEmbed);

  ngOnInit(): void {
    this.duenoId = Number(this.route.snapshot.queryParamMap.get('duenoId')) || 0;
    if (!this.duenoId) {
      this.error = 'Inicia sesión como cliente para consultar tus mascotas y reservar una cita.';
      return;
    }

    this.mascotaService.listarMascotasDeDueno(this.duenoId).subscribe({
      next: mascotas => this.mascotas = mascotas,
      error: err => this.error = err?.error?.message || 'No se pudieron cargar tus mascotas.'
    });
    this.citaService.listarEspecialidades().subscribe({
      next: especialidades => this.especialidades = especialidades,
      error: () => this.error = 'No se pudieron cargar las especialidades disponibles.'
    });
    this.cargarCitas();
  }

  alternarMascota(id: number, event: Event): void {
    const seleccionada = (event.target as HTMLInputElement).checked;
    if (seleccionada && this.mascotasSeleccionadas.size >= CitasClienteComponent.MAX_MASCOTAS_CITA) {
      (event.target as HTMLInputElement).checked = false;
      this.error = `Puedes incluir hasta ${CitasClienteComponent.MAX_MASCOTAS_CITA} mascotas en una cita.`;
      return;
    }
    if (seleccionada) this.mascotasSeleccionadas.add(id);
    else this.mascotasSeleccionadas.delete(id);
    this.error = '';
    this.limpiarHorarios();
    if (this.especialidad) this.buscarHorarios();
  }

  cambiarMotivo(motivo: string): void {
    this.motivo = motivo;
    this.clasificacion = undefined;
    this.especialidad = '';
    this.error = '';
    this.limpiarHorarios();
  }

  analizarMotivo(): void {
    this.error = '';
    this.clasificacion = undefined;
    this.especialidad = '';
    this.limpiarHorarios();
    if (this.motivo.trim().length < 8) {
      this.error = 'Describe brevemente el motivo de la cita (al menos 8 caracteres).';
      return;
    }

    this.clasificando = true;
    this.citaService.clasificarMotivo(this.motivo.trim()).subscribe({
      next: resultado => {
        this.clasificacion = resultado;
        this.especialidad = resultado.especialidad ?? '';
        this.clasificando = false;
        if (this.especialidad) this.buscarHorarios();
      },
      error: err => {
        this.clasificando = false;
        this.error = err?.error?.message || 'No se pudo analizar el motivo. Intenta de nuevo.';
      }
    });
  }

  cambiarEspecialidad(especialidad: string): void {
    this.especialidad = especialidad;
    this.limpiarHorarios();
    if (especialidad) this.buscarHorarios();
  }

  cambiarFecha(fecha: string): void {
    this.fecha = fecha;
    this.limpiarHorarios();
    if (this.especialidad) this.buscarHorarios();
  }

  buscarHorarios(): void {
    if (!this.especialidad || this.mascotasSeleccionadas.size === 0 || !this.fecha) {
      this.limpiarHorarios();
      return;
    }
    this.cargandoHorarios = true;
    this.error = '';
    this.citaService.listarDisponibilidad(
      this.fecha, this.mascotasSeleccionadas.size, this.especialidad
    ).subscribe({
      next: horarios => {
        this.horarios = horarios;
        this.cargandoHorarios = false;
        if (horarios.length === 0) this.error = 'No hay horarios para esa fecha. Prueba otro día.';
      },
      error: err => {
        this.horarios = [];
        this.cargandoHorarios = false;
        this.error = err?.error?.message || 'No se pudo consultar la disponibilidad.';
      }
    });
  }

  seleccionarHorario(inicio: string): void {
    this.horarioSeleccionado = inicio;
    this.error = '';
  }

  confirmarCita(): void {
    if (!this.horarioSeleccionado || !this.especialidad || this.mascotasSeleccionadas.size === 0) {
      this.error = 'Selecciona tus mascotas, motivo, especialidad, fecha y horario.';
      return;
    }

    const solicitud: CrearCita = {
      duenoId: this.duenoId,
      mascotaIds: [...this.mascotasSeleccionadas],
      motivo: this.motivo.trim(),
      especialidad: this.especialidad,
      fechaHoraInicio: this.horarioSeleccionado
    };
    this.guardando = true;
    this.error = '';
    this.citaService.crearCita(solicitud).subscribe({
      next: cita => {
        this.citaConfirmada = cita;
        this.guardando = false;
        this.mascotasSeleccionadas.clear();
        this.horarioSeleccionado = '';
        this.cargarCitas();
      },
      error: err => {
        this.guardando = false;
        this.error = err?.error?.message || 'No se pudo agendar. Actualiza los horarios e inténtalo de nuevo.';
        this.buscarHorarios();
      }
    });
  }

  cancelarCita(cita: Cita): void {
    if (!window.confirm('¿Quieres cancelar esta cita?')) return;
    this.citaService.cancelarDeDueno(cita.id, this.duenoId).subscribe({
      next: actualizada => {
        this.citas = this.citas.map(item => item.id === actualizada.id ? actualizada : item);
      },
      error: err => this.error = err?.error?.message || 'No se pudo cancelar la cita.'
    });
  }

  hora(valor: string): string {
    return valor.slice(11, 16);
  }

  fechaHora(valor: string): string {
    const [fecha, hora] = valor.split('T');
    const [anio, mes, dia] = fecha.split('-').map(Number);
    return `${new Date(anio, mes - 1, dia).toLocaleDateString('es-CO', {
      weekday: 'long', day: 'numeric', month: 'long'
    })} · ${hora.slice(0, 5)}`;
  }

  estadoVisible(estado: Cita['estado']): string {
    const etiquetas: Record<Cita['estado'], string> = {
      CONFIRMADA: 'Confirmada',
      CANCELADA: 'Cancelada',
      COMPLETADA: 'Completada',
      INASISTENCIA: 'Inasistencia'
    };
    return etiquetas[estado];
  }

  nombresMascotas(cita: Cita): string {
    return cita.mascotas.map(mascota => mascota.nombre).join(', ');
  }

  cancelarFlujo(): void {
    this.citaConfirmada = undefined;
    this.motivo = '';
    this.especialidad = '';
    this.clasificacion = undefined;
    this.limpiarHorarios();
  }

  private cargarCitas(): void {
    if (!this.duenoId) return;
    this.citaService.listarDeDueno(this.duenoId).subscribe({
      next: citas => this.citas = citas,
      error: () => this.error = 'No se pudo cargar tu historial de citas.'
    });
  }

  private limpiarHorarios(): void {
    this.horarios = [];
    this.horarioSeleccionado = '';
  }

  fechaMinima(): string {
    const fecha = new Date();
    return this.formatoFecha(fecha);
  }

  fechaInicial(): string {
    const fecha = new Date();
    fecha.setDate(fecha.getDate() + 1);
    while (fecha.getDay() === 0) fecha.setDate(fecha.getDate() + 1);
    return this.formatoFecha(fecha);
  }

  private formatoFecha(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }
}
