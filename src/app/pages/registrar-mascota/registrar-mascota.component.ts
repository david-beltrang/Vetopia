import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { Mascota } from '../../models/mascota.model';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioMascotaComponent } from '../../utils/formulario-mascota/formulario-mascota.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoRegistrarMascotaComponent } from './components/encabezado-registrar-mascota/encabezado-registrar-mascota.component';

@Component({
  selector: 'app-registrar-mascota',
  imports: [
    HeaderVeterinarioComponent,
    FormularioMascotaComponent,
    FooterPortalComponent,
    EncabezadoRegistrarMascotaComponent
  ],
  templateUrl: './registrar-mascota.component.html'
})
export class RegistrarMascotaComponent implements OnInit {
  veterinario?: Veterinario;
  duenoId = 0;
  dueno?: Dueno;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: (veterinario) => { this.veterinario = veterinario; },
      error: () => this.router.navigate(['/error'])
    });

    const duenoIdParam = this.route.snapshot.params['duenoId'];
    if (duenoIdParam) {
      this.duenoId = Number(duenoIdParam);
      this.duenoService.obtenerDuenoPorId(this.duenoId).subscribe({
        next: (dueno) => { this.dueno = dueno; },
        error: () => this.router.navigate(['/error'])
      });
    }
  }

  onGuardar(mascota: Mascota): void {
    this.crearMascota(mascota, () => {
      this.router.navigate(['/veterinario/mascotas'], { queryParams: { veterinarioId: this.veterinarioId } });
    });
  }

  onGuardarYLimpiar(mascota: Mascota): void {
    this.crearMascota(mascota, () => {});
  }

  private crearMascota(mascota: Mascota, alCrear: () => void): void {
    this.mascotaService.crearMascota(mascota, this.duenoId).subscribe({
      next: () => alCrear(),
      error: () => this.router.navigate(['/error'])
    });
  }
}
