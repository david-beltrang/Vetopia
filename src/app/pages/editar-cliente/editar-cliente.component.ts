import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
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
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private duenoService = inject(DuenoService);

  ngOnInit(): void {
    const idParam = this.route.snapshot.params['id'];

    if (idParam) {
      this.cargarCliente(Number(idParam));
    } else {
      this.cargando = false;
    }
  }

  private cargarCliente(id: number): void {
    this.id = id;
    const encontrado = this.duenoService.getDuenoById(id);
    this.cliente = encontrado;
    this.cargando = false;
  }

  onGuardar(dueno: Dueno): void {
    this.duenoService.updateDueno(this.id, dueno);
    this.router.navigate(['/veterinario/clientes/detalle', this.id]);
  }

  onCancelar(): void {
    if (this.id) {
      this.router.navigate(['/veterinario/clientes/detalle', this.id]);
    } else {
      this.router.navigate(['/veterinario/clientes']);
    }
  }
}