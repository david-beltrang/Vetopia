import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, of } from 'rxjs';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoMascotasComponent } from './components/encabezado-mascotas/encabezado-mascotas.component';
import { TablaMascotasComponent } from './components/tabla-mascotas/tabla-mascotas.component';

@Component({
  selector: 'app-mascotas',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    EncabezadoMascotasComponent,
    TablaMascotasComponent
  ],
  templateUrl: './mascotas.component.html'
})
export class MascotasComponent implements OnInit {
  mascotas: Mascota[] = [];
  private todasLasMascotas: Mascota[] = [];
  private mascotasAtendidasPorVeterinario = new Set<number>();
  private textoBusqueda = '';
  private mostrarSoloAtendidas = false;
  veterinario?: Veterinario;
  veterinarioId = 0;
  // Mapa id de dueño -> nombre, para mostrar la columna "Dueño" sin que el
  // dueño venga duplicado dentro de cada mascota
  duenosPorId = new Map<number, string>();

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);
  private tratamientoService = inject(TratamientoService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    // Espera las respuestas de las peticiones independientes.
    // Así se construye el listado con mascotas, dueños y veterinario.
    combineLatest({
      mascotas: this.mascotaService.listarMascotas(),
      duenos: this.duenoService.listarDuenos(),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId),
      tratamientos: this.veterinarioId > 0
        ? this.tratamientoService.listarHistorialDeVeterinario(this.veterinarioId)
        : of([])
    }).subscribe({
      next: ({ mascotas, duenos, veterinario, tratamientos }) => {
        this.todasLasMascotas = mascotas;
        // Guarda los pacientes que tienen historial con el veterinario actual.
        for (const item of tratamientos) {
          const mascotaId = item.tratamiento.mascota?.id;
          if (mascotaId != null) {
            this.mascotasAtendidasPorVeterinario.add(mascotaId);
          }
        }
        this.aplicarFiltros();
        this.veterinario = veterinario;
        const mapaDuenos = new Map<number, string>();
        for (const dueno of duenos) {
          if (dueno.id != null) {
            mapaDuenos.set(dueno.id, dueno.nombre);
          }
        }
        this.duenosPorId = mapaDuenos;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  filtrarPorNombre(texto: string): void {
    this.textoBusqueda = texto;
    this.aplicarFiltros();
  }

  cambiarFiltroAtendidas(activado: boolean): void {
    this.mostrarSoloAtendidas = activado;
    this.aplicarFiltros();
  }

  private aplicarFiltros(): void {
    // Se filtra sobre la lista original para poder combinar ambos criterios.
    const termino = this.textoBusqueda.trim().toLocaleLowerCase();
    this.mascotas = this.todasLasMascotas.filter(mascota => {
      const coincideNombre = !termino || mascota.nombre.toLocaleLowerCase().includes(termino);
      const coincideVeterinario = !this.mostrarSoloAtendidas ||
        (mascota.id != null && this.mascotasAtendidasPorVeterinario.has(mascota.id));
      return coincideNombre && coincideVeterinario;
    });
  }

  get totalMascotas(): number {
    return this.mascotas.length;
  }

  get textoContador(): string {
    return this.totalMascotas === 1 ? '1 mascota registrada' : `${this.totalMascotas} mascotas registradas`;
  }

  verFicha(mascota: Mascota): void {
    this.router.navigate(['/veterinario/mascotas/ficha', mascota.id], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }
}
