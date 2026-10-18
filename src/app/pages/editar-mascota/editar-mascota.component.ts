import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioMascotaComponent } from '../../utils/formulario-mascota/formulario-mascota.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoEditarMascotaComponent } from './components/encabezado-editar-mascota/encabezado-editar-mascota.component';
import { CargandoMascotaComponent } from './components/cargando-mascota/cargando-mascota.component';
import { MascotaNoEncontradaComponent } from './components/mascota-no-encontrada/mascota-no-encontrada.component';

@Component({
  selector: 'app-editar-mascota',
  imports: [
    HeaderVeterinarioComponent,
    FormularioMascotaComponent,
    FooterPortalComponent,
    EncabezadoEditarMascotaComponent,
    CargandoMascotaComponent,
    MascotaNoEncontradaComponent
  ],
  templateUrl: './editar-mascota.component.html'
})
export class EditarMascotaComponent implements OnInit {
  cargando: boolean = true;
  id: number = 0;
  mascota?: Mascota;
  veterinario?: Veterinario;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: (veterinario) => { this.veterinario = veterinario; },
      error: () => this.router.navigate(['/error'])
    });

    const idParam = this.route.snapshot.params['id'];
    if (idParam) {
      this.cargarMascota(Number(idParam));
    } else {
      this.cargando = false;
    }
  }

  private cargarMascota(id: number): void {
    this.id = id;
    this.mascotaService.obtenerMascotaPorId(id).subscribe({
      next: (mascota) => {
        this.mascota = mascota;
        this.cargando = false;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  onGuardar(mascota: Mascota): void {
    this.mascotaService.actualizarMascota(this.id, mascota).subscribe({
      next: () => this.router.navigate(['/veterinario/mascotas/ficha', this.id], {
        queryParams: { veterinarioId: this.veterinarioId }
      }),
      error: () => this.router.navigate(['/error'])
    });
  }

  onCancelar(): void {
    if (this.id) {
      this.router.navigate(['/veterinario/mascotas/ficha', this.id], {
        queryParams: { veterinarioId: this.veterinarioId }
      });
    } else {
      this.router.navigate(['/veterinario/mascotas'], {
        queryParams: { veterinarioId: this.veterinarioId }
      });
    }
  }
}
