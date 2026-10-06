import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioMascotaComponent } from '../../utils/formulario-mascota/formulario-mascota.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';

@Component({
  selector: 'app-editar-mascota',
  imports: [
    HeaderVeterinarioComponent,
    FormularioMascotaComponent,
    FooterPortalComponent
  ],
  templateUrl: './editar-mascota.component.html'
})
export class EditarMascotaComponent implements OnInit {
  cargando: boolean = true;
  id: number = 0;
  mascota?: Mascota;
  veterinario?: Veterinario;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    const idParam = this.route.snapshot.params['id'];

    if (idParam) {
      this.cargarMascota(Number(idParam));
    } else {
      this.cargando = false;
    }
  }

  private cargarMascota(id: number): void {
    this.id = id;
    const encontrada = this.mascotaService.getMascotaById(id);
    this.mascota = encontrada;
    this.cargando = false;
  }

  onGuardar(mascota: Mascota): void {
    this.mascotaService.updateMascota(this.id, mascota);
    this.router.navigate(['/veterinario/mascotas/ficha', this.id]);
  }

  onCancelar(): void {
    if (this.id) {
      this.router.navigate(['/veterinario/mascotas/ficha', this.id]);
    } else {
      this.router.navigate(['/veterinario/mascotas']);
    }
  }
}
