import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioMascotaComponent } from './components/formulario-mascota/formulario-mascota.component';
import { FooterComponent } from '../../utils/footer/footer.component';

@Component({
  selector: 'app-editar-mascota',
  imports: [
    CommonModule,
    HeaderVeterinarioComponent,
    FormularioMascotaComponent,
    FooterComponent
  ],
  templateUrl: './editar-mascota.component.html'
})
export class EditarMascotaComponent implements OnInit {
  mascota: Mascota | null = null;
  cargando: boolean = true;
  veterinario: Veterinario | undefined = undefined;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const id = Number(params['id']);

      if (id) {
        this.cargarMascota(id);
      } else {
        this.cargando = false;
      }
    });
  }

  private cargarMascota(id: number): void {
    this.cargando = true;
    this.mascota = this.mascotaService.getMascotaById(id) || null;
    this.cargando = false;
  }

  onGuardar(): void {
    if (this.mascota) {
      this.mascotaService.updateMascota(this.mascota.id, this.mascota);
      this.router.navigate(['/veterinario/mascotas/ficha'], {
        queryParams: { id: this.mascota.id }
      });
    }
  }

  onCancelar(): void {
    this.router.navigate(['/veterinario/mascotas/ficha'], {
      queryParams: { id: this.mascota?.id }
    });
  }
}
