import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { TratamientoService } from '../../service/tratamiento.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { Veterinario } from '../../models/veterinario.model';
import { TratamientoDetalle } from '../../models/ficha-clinica.model';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { ItemTratamientoVeterinarioComponent } from './components/item-tratamiento-veterinario/item-tratamiento-veterinario.component';

@Component({
  selector: 'app-tratamientos',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    ItemTratamientoVeterinarioComponent
  ],
  templateUrl: './tratamientos.component.html'
})
export class TratamientosComponent implements OnInit {
  veterinario?: Veterinario;
  tratamientos: TratamientoDetalle[] = [];
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private veterinarioService = inject(VeterinarioService);
  private tratamientoService = inject(TratamientoService);

  ngOnInit(): void {
    // El veterinario viaja en la URL para conservar la sesión al recargar.
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: (veterinario) => { this.veterinario = veterinario; },
      error: () => this.router.navigate(['/error'])
    });

    // Sin id no hay historial que pedir.
    if (this.veterinarioId > 0) {
      this.tratamientoService.listarHistorialDeVeterinario(this.veterinarioId).subscribe({
        next: (tratamientos) => { this.tratamientos = tratamientos; },
        error: () => this.router.navigate(['/error'])
      });
    }
  }
}
