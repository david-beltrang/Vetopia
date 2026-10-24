import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { combineLatest, map, mergeMap } from 'rxjs';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { VeterinarioService } from '../../service/veterinario.service';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';
import { CabeceraClienteComponent } from './components/cabecera-cliente/cabecera-cliente.component';
import { DatosContactoClienteComponent } from './components/datos-contacto-cliente/datos-contacto-cliente.component';
import { MascotasClienteComponent } from './components/mascotas-cliente/mascotas-cliente.component';
import { AccionesClienteComponent } from './components/acciones-cliente/acciones-cliente.component';
import { ClienteNoEncontradoComponent } from './components/cliente-no-encontrado/cliente-no-encontrado.component';

@Component({
  selector: 'app-detalle-cliente',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent,
    CabeceraClienteComponent,
    DatosContactoClienteComponent,
    MascotasClienteComponent,
    AccionesClienteComponent,
    ClienteNoEncontradoComponent
  ],
  templateUrl: './detalle-cliente.component.html'
})
export class DetalleClienteComponent implements OnInit {
  cargando: boolean = true;
  id: number = 0;
  cliente?: Dueno;
  mascotas: Mascota[] = [];
  veterinario?: Veterinario;
  veterinarioId = 0;

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private duenoService = inject(DuenoService);
  private mascotaService = inject(MascotaService);
  private veterinarioService = inject(VeterinarioService);

  ngOnInit(): void {
    this.veterinarioId = Number(this.route.snapshot.queryParamMap.get('veterinarioId')) || 0;

    const idParam = this.route.snapshot.params['id'];
    if (idParam) {
      this.id = Number(idParam);
      this.cargarCliente(this.id);
    } else {
      this.cargando = false;
    }
  }

  private cargarCliente(id: number): void {
    // Espera los datos del cliente y veterinario antes de cargar sus mascotas.
    combineLatest({
      cliente: this.duenoService.obtenerDuenoPorId(id),
      veterinario: this.veterinarioService.obtenerVeterinarioOpcional(this.veterinarioId)
    }).pipe(
      mergeMap(({ cliente, veterinario }) =>
        this.mascotaService.listarMascotasDeDueno(id).pipe(
          map(mascotas => ({ cliente, veterinario, mascotas }))
        )
      )
    ).subscribe({
      next: ({ cliente, veterinario, mascotas }) => {
        this.cliente = cliente;
        this.veterinario = veterinario;
        this.mascotas = mascotas;
        this.cargando = false;
      },
      error: () => this.router.navigate(['/error'])
    });
  }

  volverClientes(): void {
    this.router.navigate(['/veterinario/clientes'], { queryParams: { veterinarioId: this.veterinarioId } });
  }

  verFicha(mascota: Mascota): void {
    this.router.navigate(['/veterinario/mascotas/ficha', mascota.id], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }

  editarCliente(): void {
    this.router.navigate(['/veterinario/clientes/editar', this.id], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }

  agregarMascota(): void {
    this.router.navigate(['/veterinario/mascotas/registrar-mascota', this.id], {
      queryParams: { veterinarioId: this.veterinarioId }
    });
  }

  eliminarCliente(): void {
    const cliente = this.cliente;
    if (!cliente) return;

    const confirmado = window.confirm(`¿Seguro que deseas eliminar a ${cliente.nombre}? Esta acción no se puede deshacer.`);
    if (!confirmado) return;

    this.duenoService.eliminarDueno(this.id).subscribe({
      next: () => this.router.navigate(['/veterinario/clientes'], { queryParams: { veterinarioId: this.veterinarioId } }),
      error: () => this.router.navigate(['/error'])
    });
  }
}
