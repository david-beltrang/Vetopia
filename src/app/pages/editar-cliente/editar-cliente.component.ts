import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest } from 'rxjs';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FormularioClienteComponent } from '../../utils/formulario-cliente/formulario-cliente.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { EncabezadoEditarClienteComponent } from './components/encabezado-editar-cliente/encabezado-editar-cliente.component';
import { CargandoClienteComponent } from './components/cargando-cliente/cargando-cliente.component';
import { ClienteNoEncontradoComponent } from './components/cliente-no-encontrado/cliente-no-encontrado.component';

@Component({
  selector: 'app-editar-cliente',
  imports: [
    HeaderVeterinarioComponent,
    FormularioClienteComponent,
    FooterPortalComponent,
    EncabezadoEditarClienteComponent,
    CargandoClienteComponent,
    ClienteNoEncontradoComponent
  ],
  templateUrl: './editar-cliente.component.html'
})
export class EditarClienteComponent implements OnInit {
  cargando: boolean = true;
  id: number = 0;
  cliente?: Dueno;
  veterinario?: Veterinario;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private duenoService = inject(DuenoService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    const idParam = this.route.snapshot.params['id'];
    if (idParam) {
      this.cargarCliente(Number(idParam));
    } else {
      this.cargando = false;
    }
  }

  private cargarCliente(id: number): void {
    this.id = id;
    // Espera las respuestas de ambas peticiones independientes.
    combineLatest({
      cliente: this.duenoService.obtenerDuenoPorId(id),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).subscribe({
      next: ({ cliente, veterinario }) => {
        this.cliente = cliente;
        this.veterinario = veterinario;
        this.cargando = false;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  onGuardar(dueno: Dueno): void {
    this.duenoService.actualizarDueno(this.id, dueno).subscribe({
      next: () => this.router.navigate(['/veterinario/clientes/detalle', this.id], {
        queryParams: { veterinarioId: this.veterinarioId }
      }),
      error: () => this.router.navigate(['/error'])
    });
  }

  onCancelar(): void {
    if (this.id) {
      this.router.navigate(['/veterinario/clientes/detalle', this.id], {
        queryParams: { veterinarioId: this.veterinarioId }
      });
    } else {
      this.router.navigate(['/veterinario/clientes'], {
        queryParams: { veterinarioId: this.veterinarioId }
      });
    }
  }
}
