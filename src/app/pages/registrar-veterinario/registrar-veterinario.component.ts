import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Veterinario } from '../../models/veterinario.model';
import { VeterinarioService } from '../../service/veterinario.service';
import { HeaderAdminComponent } from '../../utils/header-admin/header-admin.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoRegistrarVeterinarioComponent } from './components/encabezado-registrar-veterinario/encabezado-registrar-veterinario.component';
import { FormularioRegistrarVeterinarioComponent } from './components/formulario-registrar-veterinario/formulario-registrar-veterinario.component';

@Component({
  selector: 'app-registrar-veterinario',
  imports: [
    HeaderAdminComponent,
    FooterPortalComponent,
    EncabezadoRegistrarVeterinarioComponent,
    FormularioRegistrarVeterinarioComponent
  ],
  templateUrl: './registrar-veterinario.component.html'
})
// Coordina el registro y navegación del nuevo veterinario.
export class RegistrarVeterinarioComponent implements OnInit {
  adminId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.adminId = Number(this.route.snapshot.queryParamMap.get('adminId')) || 0;
  }

  // Guarda el veterinario y vuelve al listado del administrador.
  onGuardar(veterinario: Veterinario): void {
    this.veterinarioService.crearVeterinario(veterinario).subscribe({
      next: () => this.router.navigate(['/admin/veterinarios'], {
        queryParams: { 
          adminId: this.adminId 
        } }),
      error: () => this.router.navigate(['/error'])
    });
  }

  onCancelar(): void {
    this.router.navigate(['/admin/veterinarios'], {
      queryParams: {
        adminId: this.adminId 
      } });
  }
}
