import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioClienteComponent } from '../../utils/formulario-cliente/formulario-cliente.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoRegistrarClienteComponent } from './components/encabezado-registrar-cliente/encabezado-registrar-cliente.component';

@Component({
  selector: 'app-registrar-cliente',
  imports: [
    HeaderVeterinarioComponent,
    FormularioClienteComponent,
    FooterPortalComponent,
    EncabezadoRegistrarClienteComponent
  ],
  templateUrl: './registrar-cliente.component.html'
})
export class RegistrarClienteComponent implements OnInit {
  veterinario?: Veterinario;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId).subscribe({
      next: (veterinario) => { this.veterinario = veterinario; },
      error: () => this.router.navigate(['/error'])
    });
  }

  onGuardar(dueno: Dueno): void {
    this.duenoService.crearDueno(dueno).subscribe({
      next: (nuevoDueno) => this.router.navigate(['/veterinario/mascotas/registrar-mascota', nuevoDueno.id], {
        queryParams: { veterinarioId: this.veterinarioId }
      }),
      error: () => this.router.navigate(['/error'])
    });
  }

  onCancelar(): void {
    this.router.navigate(['/veterinario/clientes'], { queryParams: { veterinarioId: this.veterinarioId } });
  }
}
