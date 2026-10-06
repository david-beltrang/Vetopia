import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioClienteComponent } from '../../utils/formulario-cliente/formulario-cliente.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';

@Component({
  selector: 'app-registrar-cliente',
  imports: [
    HeaderVeterinarioComponent,
    FormularioClienteComponent,
    FooterPortalComponent
  ],
  templateUrl: './registrar-cliente.component.html'
})
export class RegistrarClienteComponent {
  veterinario?: Veterinario;
  private router = inject(Router);
  private duenoService = inject(DuenoService);

  onGuardar(dueno: Dueno): void {
    const nuevoDueno = this.duenoService.createDueno(dueno);
    this.router.navigate(['/veterinario/mascotas/registrar-mascota', nuevoDueno.id]);
  }

  onCancelar(): void {
    this.router.navigate(['/veterinario/clientes']);
  }
}