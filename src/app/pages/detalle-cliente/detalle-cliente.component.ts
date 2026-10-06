import { Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { Veterinario } from '../../models/veterinario.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { HeaderVeterinarioComponent } from '../ficha-clinica/components/header-veterinario/header-veterinario.component';
import { FooterPortalComponent } from '../../utils/footer-portal/footer-portal.component';

@Component({
  selector: 'app-detalle-cliente',
  imports: [
    HeaderVeterinarioComponent,
    FooterPortalComponent
  ],
  templateUrl: './detalle-cliente.component.html'
})
export class DetalleClienteComponent implements OnInit {
  cargando: boolean = true;
  id: number = 0;
  cliente?: Dueno;
  mascotas: Mascota[] = [];
  veterinario?: Veterinario;
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private duenoService = inject(DuenoService);
  private mascotaService = inject(MascotaService);

  ngOnInit(): void {
    const idParam = this.route.snapshot.params['id'];

    if (idParam) {
      this.id = Number(idParam);
      this.cargarCliente(this.id);
    } else {
      this.cargando = false;
    }
  }

  private cargarCliente(id: number): void {
    const encontrado = this.duenoService.getDuenoById(id);
    this.cliente = encontrado;
    this.mascotas = this.mascotaService.getMascotasDeDueno(id);
    this.cargando = false;
  }

  iniciales(): string {
    const nombre = this.cliente?.nombre ?? '';
    return nombre ? nombre.slice(0, 2).toUpperCase() : '';
  }

  volverClientes(): void {
    this.router.navigate(['/veterinario/clientes']);
  }

  verFicha(mascota: Mascota): void {
    this.router.navigate(['/veterinario/mascotas/ficha', mascota.id]);
  }

  editarCliente(): void {
    this.router.navigate(['/veterinario/clientes/editar', this.id]);
  }

  agregarMascota(): void {
    this.router.navigate(['/veterinario/mascotas/registrar-mascota', this.id]);
  }

  eliminarCliente(): void {
    const cliente = this.cliente;
    if (!cliente) return;

    const confirmado = window.confirm(`¿Seguro que deseas eliminar a ${cliente.nombre}? Esta acción no se puede deshacer.`);
    if (!confirmado) return;

    this.duenoService.eliminarCliente(this.id);
    this.router.navigate(['/veterinario/clientes']);
  }
}