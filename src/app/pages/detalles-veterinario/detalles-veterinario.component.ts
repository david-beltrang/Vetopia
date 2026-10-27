import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { VeterinarioService } from '../../service/veterinario.service';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderAdminComponent } from '../../utils/header-admin/header-admin.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { DatosVeterinarioComponent } from './components/datos-veterinario/datos-veterinario.component';
import { EstadoVeterinarioComponent } from './components/estado-veterinario/estado-veterinario.component';
import { AccionesVeterinarioComponent } from './components/acciones-veterinario/acciones-veterinario.component';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'app-detalles-veterinario',
  imports: [
    HeaderAdminComponent,
    FooterPortalComponent,
    DatosVeterinarioComponent,
    EstadoVeterinarioComponent,
    AccionesVeterinarioComponent
  ],
  templateUrl: './detalles-veterinario.component.html'
})
// Coordina la información y acciones del veterinario
export class DetallesVeterinarioComponent implements OnInit {
  veterinario?: Veterinario;
  veterinarioId = 0;
  adminId = 0;
  cargando = true;
  mensajeError = '';

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private veterinarioService = inject(VeterinarioService);

  // Inicializa el componente y carga los datos del veterinario
  ngOnInit(): void {
    this.adminId = Number(this.route.snapshot.queryParamMap.get('adminId')) || 0;
    const idParam = this.route.snapshot.params['id'];
    // Si se proporciona un ID de veterinario, cargar sus datos; de lo contrario, mostrar un mensaje de error
    if (idParam) {
      this.veterinarioId = Number(idParam);
      this.cargarVeterinario(this.veterinarioId);
      // Si el ID no es válido, mostrar un mensaje de error
    } else {
      this.cargando = false;
      this.mensajeError = 'Parámetros inválidos';
    }
  }

  // Carga los datos del veterinario y el número de atenciones desde el servicio
  private cargarVeterinario(id: number): void {
    // Combina las dos respuestas antes de mostrar el detalle completo.
    combineLatest({
      veterinario: this.veterinarioService.obtenerVeterinarioPorId(id),
      atenciones: this.veterinarioService.contarAtenciones(id)
    }).subscribe({
      next: ({ veterinario, atenciones }) => {
        this.veterinario = { ...veterinario, numeroAtenciones: atenciones };
        this.cargando = false;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  // 
  onEstadoCambiado(veterinario: Veterinario): void {
    if (veterinario.id == null)
      return;
    // 
    this.veterinarioService.cambiarEstado(veterinario.id, veterinario.estado).subscribe({
      next: () => {
        this.veterinario = veterinario;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  navigateToDashboard(): void {
    this.router.navigate(['/admin/dashboard'], { queryParams: { adminId: this.adminId } });
  }

  eliminarVeterinario(): void {
    const veterinario = this.veterinario;
    if (!veterinario || !veterinario.id)
      return;

    const confirmado = window.confirm(`¿Seguro que deseas eliminar a ${veterinario.nombre}? Esta acción no se puede deshacer.`);
    if (!confirmado)
      return;

    this.veterinarioService.eliminarVeterinario(veterinario.id).subscribe({
      next: () => this.router.navigate(['/admin/veterinarios'], { queryParams: { adminId: this.adminId } }),
      error: () => this.router.navigate(['/error'])
    });
  }
}
