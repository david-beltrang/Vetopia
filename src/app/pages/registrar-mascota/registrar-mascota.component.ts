import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioRegistroComponent } from './components/formulario-registro/formulario-registro.component';
import { FooterComponent } from '../../utils/footer/footer.component';

@Component({
  selector: 'app-registrar-mascota',
  imports: [
    CommonModule,
    HeaderVeterinarioComponent,
    FormularioRegistroComponent,
    FooterComponent
  ],
  templateUrl: './registrar-mascota.component.html'
})
export class RegistrarMascotaComponent implements OnInit {
  mascotas: Mascota[] = [];
  veterinario: Veterinario | undefined = undefined;
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    this.mascotas = this.mascotaService.getMascotas();
  }

  onGuardar(mascota: Mascota): void {
    const nuevaMascota = this.mascotaService.createMascota(mascota);
    this.mascotas = this.mascotaService.getMascotas();
    this.router.navigate(['/veterinario/mascotas/ficha'], {
      queryParams: { id: nuevaMascota.id }
    });
  }

  onGuardarYLimpiar(mascota: Mascota): void {
    this.mascotaService.createMascota(mascota);
    this.mascotas = this.mascotaService.getMascotas();
  }
}
