import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Veterinario } from '../../models/veterinario.model';
import { VeterinarioService } from '../../service/veterinario.service';
import { HeaderAdminComponent } from '../../utils/header-admin/header-admin.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoVeterinariosComponent } from './components/encabezado-veterinarios/encabezado-veterinarios.component';
import { TablaVeterinariosComponent } from './components/tabla-veterinarios/tabla-veterinarios.component';

@Component({
  selector: 'app-listar-veterinarios',
  imports: [
    HeaderAdminComponent,
    FooterPortalComponent,
    EncabezadoVeterinariosComponent,
    TablaVeterinariosComponent
  ],
  templateUrl: './listar-veterinarios.component.html'
})
// Gestiona la carga, búsqueda y navegación de veterinarios.
export class ListarVeterinariosComponent implements OnInit {
  veterinarios: Veterinario[] = [];
  private todosLosVeterinarios: Veterinario[] = [];
  adminId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.adminId = Number(this.route.snapshot.queryParamMap.get('adminId')) || 0;

    this.veterinarioService.listarVeterinarios().subscribe({
      next: (veterinarios) => {
        this.todosLosVeterinarios = veterinarios;
        this.veterinarios = veterinarios;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  filtrarVeterinarios(texto: string): void {
    const termino = texto.trim().toLocaleLowerCase();
    if (!termino) {
      this.veterinarios = this.todosLosVeterinarios;
      return;
    }

    this.veterinarios = this.todosLosVeterinarios.filter(veterinario =>
      veterinario.nombre.toLocaleLowerCase().includes(termino) ||
      veterinario.cedula.toLocaleLowerCase().includes(termino)
    );
  }

  get totalVeterinarios(): number {
    return this.veterinarios.length;
  }

  get textoContador(): string {
    return this.totalVeterinarios === 1 ? '1 VETERINARIO REGISTRADO' : `${this.totalVeterinarios} VETERINARIOS REGISTRADOS`;
  }

  verDetalle(veterinario: Veterinario): void {
    this.router.navigate(['/admin/detalles-veterinario', veterinario.id], {
      queryParams: { adminId: this.adminId }
    });
  }

  nuevoVeterinario(): void {
    this.router.navigate(['/admin/registrar-veterinario'], { queryParams: { adminId: this.adminId } });
  }
}
