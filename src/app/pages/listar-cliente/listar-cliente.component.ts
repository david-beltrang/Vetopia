import { Component, OnInit, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
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
  private router = inject(Router);
  private duenoService = inject(DuenoService);

  ngOnInit(): void {
    this.clientes = this.duenoService.listarDuenos();
  }

  get totalClientes(): number {
    return this.clientes.length;
  }

  get textoContador(): string {
    return this.totalClientes === 1 ? '1 CLIENTE REGISTRADO' : `${this.totalClientes} CLIENTES REGISTRADOS`;
  }

  verDetalle(cliente: Dueno): void {
    this.router.navigate(['/veterinario/clientes/detalle', cliente.id]);
  }

  nuevoCliente(): void {
    this.router.navigate(['/veterinario/mascotas/registrar-cliente']);
  }
}