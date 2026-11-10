import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Cita } from '../../models/cita.model';
import { Veterinario } from '../../models/veterinario.model';
import { CitaService } from '../../service/cita.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { AgendaSemanalComponent, CambioEstadoCita } from '../citas/components/agenda-semanal/agenda-semanal.component';

@Component({
  selector: 'app-citas-veterinario',
  imports: [CommonModule, HeaderVeterinarioComponent, FooterPortalComponent, AgendaSemanalComponent],
  templateUrl: './citas-veterinario.component.html'
})
export class CitasVeterinarioComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private citaService = inject(CitaService);
  private veterinarioService = inject(VeterinarioService);

  veterinarioId = 0;
  veterinario?: Veterinario;
  citas: Cita[] = [];
  dias: Date[] = [];
  lunes = new Date();
  cargando = false;
  error = '';

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;
    if (!this.veterinarioId) {
      this.error = 'No se identificó al veterinario. Inicia sesión nuevamente.';
      return;
    }
    this.lunes = this.inicioSemana(new Date());
    this.dias = this.generarDias(this.lunes);
    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: veterinario => this.veterinario = veterinario,
      error: () => this.router.navigate(['/error'])
    });
    this.cargarCitas();
  }

  moverSemana(cantidad: number): void {
    this.lunes = new Date(this.lunes);
    this.lunes.setDate(this.lunes.getDate() + cantidad * 7);
    this.dias = this.generarDias(this.lunes);
    this.cargarCitas();
  }

  cambiarEstado(evento: CambioEstadoCita): void {
    this.citaService.actualizarEstado(evento.citaId, this.veterinarioId, evento.estado).subscribe({
      next: actualizada => this.reemplazarCita(actualizada),
      error: err => this.error = err?.error?.message || 'No se pudo actualizar la cita.'
    });
  }

  fechaVisible(): string {
    if (this.dias.length < 6) return '';
    const primero = this.dias[0].toLocaleDateString('es-CO', { day: 'numeric', month: 'long' });
    const ultimo = this.dias[5].toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' });
    return `${primero} – ${ultimo}`;
  }

  get confirmadas(): number {
    return this.citas.filter(cita => cita.estado === 'CONFIRMADA').length;
  }

  get completadas(): number {
    return this.citas.filter(cita => cita.estado === 'COMPLETADA').length;
  }

  private cargarCitas(): void {
    this.cargando = true;
    this.error = '';
    this.citaService.listarDeVeterinario(
      this.veterinarioId, this.fechaClave(this.lunes), this.fechaClave(this.dias[5])
    ).subscribe({
      next: citas => {
        this.citas = citas;
        this.cargando = false;
      },
      error: err => {
        this.cargando = false;
        this.error = err?.error?.message || 'No se pudo cargar la agenda.';
      }
    });
  }

  private reemplazarCita(actualizada: Cita): void {
    this.citas = this.citas.map(cita => cita.id === actualizada.id ? actualizada : cita);
  }

  private inicioSemana(fecha: Date): Date {
    const resultado = new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate());
    const dia = resultado.getDay();
    resultado.setDate(resultado.getDate() - (dia === 0 ? 6 : dia - 1));
    return resultado;
  }

  private generarDias(lunes: Date): Date[] {
    return Array.from({ length: 6 }, (_, indice) => {
      const dia = new Date(lunes);
      dia.setDate(dia.getDate() + indice);
      return dia;
    });
  }

  private fechaClave(fecha: Date): string {
    const anio = fecha.getFullYear();
    const mes = String(fecha.getMonth() + 1).padStart(2, '0');
    const dia = String(fecha.getDate()).padStart(2, '0');
    return `${anio}-${mes}-${dia}`;
  }
}
