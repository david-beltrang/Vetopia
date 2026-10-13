import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { DuenoService } from '../../service/dueno.service';
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
  mascotas: Mascota[] = [];
  veterinario?: Veterinario;
  duenoId = 0;
  dueno?: Dueno;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);
  private duenoService = inject(DuenoService);

  ngOnInit(): void {
    const duenoIdParam = this.route.snapshot.params['duenoId'];

    if (duenoIdParam) {
      this.duenoId = Number(duenoIdParam);
      this.dueno = this.duenoService.getDuenoById(this.duenoId);
    }

    this.mascotas = this.mascotaService.getMascotas();
  }

  onGuardar(mascota: Mascota): void {
    mascota.dueno = this.dueno ?? mascota.dueno;
    this.mascotaService.createMascota(mascota);
    this.mascotas = this.mascotaService.getMascotas();
    this.router.navigate(['/veterinario/mascotas']);
  }

  onGuardarYLimpiar(mascota: Mascota): void {
    mascota.dueno = this.dueno ?? mascota.dueno;
    this.mascotaService.createMascota(mascota);
    this.mascotas = this.mascotaService.getMascotas();
  }
}