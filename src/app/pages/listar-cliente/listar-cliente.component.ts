import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoClientesComponent } from './components/encabezado-clientes/encabezado-clientes.component';
import { TablaClientesComponent } from './components/tabla-clientes/tabla-clientes.component';

@Component({
  selector: 'app-listar-cliente',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    EncabezadoClientesComponent,
    TablaClientesComponent
  ],
  templateUrl: './listar-cliente.component.html'
})
export class ListarClienteComponent implements OnInit {
  clientes: Dueno[] = [];
  veterinario?: Veterinario;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    // Espera las respuestas de ambas peticiones independientes.
    combineLatest({
      clientes: this.duenoService.listarDuenos(),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).subscribe({
      next: ({ clientes, veterinario }) => {
        this.clientes = clientes;
        this.veterinario = veterinario;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  get totalClientes(): number {
    return this.clientes.length;
  }

  get textoContador(): string {
    return this.totalClientes === 1 ? '1 CLIENTE REGISTRADO' : `${this.totalClientes} CLIENTES REGISTRADOS`;
  }

  verDetalle(cliente: Dueno): void {
    this.router.navigate(['/veterinario/clientes/detalle', cliente.id], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }

  nuevoCliente(): void {
    this.router.navigate(['/veterinario/mascotas/registrar-cliente'], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }
}
