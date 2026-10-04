import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MascotaService } from '../../service/mascota.service';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterComponent } from '../../utils/footer/footer.component';

@Component({
  selector: 'app-mascotas',
  imports: [
    CommonModule,
    HeaderVeterinarioComponent,
    FooterComponent
  ],
  templateUrl: './mascotas.component.html'
})
export class MascotasComponent implements OnInit {
  mascotas: Mascota[] = [];
  veterinario: Veterinario | undefined = undefined;
  private router = inject(Router);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    this.mascotas = this.mascotaService.getMascotas();
  }

  get totalMascotas(): number {
    return this.mascotas.length;
  }

  get textoContador(): string {
    return this.totalMascotas === 1 ? '1 mascota registrada' : `${this.totalMascotas} mascotas registradas`;
  }

  getEdad(mascota: Mascota): string {
    return `${mascota.edad} años`;
  }

  getDueno(mascota: Mascota): string {
    return mascota.dueno?.nombre || 'No registrado';
  }

  getNumeroPaciente(id: number): string {
    return `PACIENTE N° ${id.toString().padStart(4, '0')}`;
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement;
    img.src = 'https://via.placeholder.com/56?text=No+Image';
  }

  verFicha(mascota: Mascota): void {
    this.router.navigate(['/veterinario/mascotas/ficha'], {
      queryParams: { id: mascota.id }
    });
  }
}
