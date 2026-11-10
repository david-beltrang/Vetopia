import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Cita } from '../../models/cita.model';
import { Veterinario } from '../../models/veterinario.model';
import { CitaService } from '../../service/cita.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { HeaderAdminComponent } from '../../utils/header-admin/header-admin.component';
import { AgendaSemanalComponent } from '../citas/components/agenda-semanal/agenda-semanal.component';

@Component({
  selector: 'app-citas-admin',
  imports: [CommonModule, FormsModule, HeaderAdminComponent, FooterPortalComponent, AgendaSemanalComponent],
  templateUrl: './citas-admin.component.html'
})
export class CitasAdminComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private citaService = inject(CitaService);
  private veterinarioService = inject(VeterinarioService);

  adminId = 0;
  citas: Cita[] = [];
  veterinarios: Veterinario[] = [];
  dias: Date[] = [];
  lunes = new Date();
  veterinarioFiltro = '';
  estadoFiltro = '';
  cargando = false;
  error = '';

  ngOnInit(): void {
    this.adminId = Number(this.route.snapshot.queryParamMap.get('adminId')) || 0;
    this.lunes = this.inicioSemana(new Date());
    this.dias = this.generarDias(this.lunes);
    this.cargarCitas();
    this.veterinarioService.listarVeterinarios().subscribe({
      next: veterinarios => this.veterinarios = veterinarios,
      error: () => this.error = 'No se pudo cargar la lista de veterinarios.'
    });
  }

  moverSemana(cantidad: number): void {
    this.lunes = new Date(this.lunes);
    this.lunes.setDate(this.lunes.getDate() + cantidad * 7);
    this.dias = this.generarDias(this.lunes);
    this.cargarCitas();
  }

  get citasFiltradas(): Cita[] {
    return this.citas.filter(cita =>
      (!this.veterinarioFiltro || String(cita.veterinarioId) === this.veterinarioFiltro)
      && (!this.estadoFiltro || cita.estado === this.estadoFiltro));
  }

  get confirmadas(): number {
    return this.citasFiltradas.filter(cita => cita.estado === 'CONFIRMADA').length;
  }

  get canceladas(): number {
    return this.citasFiltradas.filter(cita => cita.estado === 'CANCELADA').length;
  }

  get inasistencias(): number {
    return this.citasFiltradas.filter(cita => cita.estado === 'INASISTENCIA').length;
  }

  fechaVisible(): string {
    if (this.dias.length < 6) return '';
    const primero = this.dias[0].toLocaleDateString('es-CO', { day: 'numeric', month: 'long' });
    const ultimo = this.dias[5].toLocaleDateString('es-CO', { day: 'numeric', month: 'long', year: 'numeric' });
    return `${primero} – ${ultimo}`;
  }

  cancelarCita(citaId: number): void {
    if (!window.confirm('¿Cancelar esta cita desde la agenda administrativa?')) return;
    this.citaService.cancelarComoAdministrador(citaId).subscribe({
      next: actualizada => this.citas = this.citas.map(cita => cita.id === actualizada.id ? actualizada : cita),
      error: err => this.error = err?.error?.message || 'No se pudo cancelar la cita.'
    });
  }

  private cargarCitas(): void {
    this.cargando = true;
    this.error = '';
    this.citaService.listarTodas(this.fechaClave(this.lunes), this.fechaClave(this.dias[5])).subscribe({
      next: citas => {
        this.citas = citas;
        this.cargando = false;
      },
      error: err => {
        this.cargando = false;
        this.error = err?.error?.message || 'No se pudo cargar el calendario general.';
      }
    });
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
