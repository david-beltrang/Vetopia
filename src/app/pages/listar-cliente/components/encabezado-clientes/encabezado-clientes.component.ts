import { Component, output } from '@angular/core';

@Component({
  selector: 'app-encabezado-clientes',
  templateUrl: './encabezado-clientes.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoClientesComponent {
  nuevoCliente = output<void>();
  busqueda = output<string>();

  actualizarBusqueda(event: Event): void {
    this.busqueda.emit((event.target as HTMLInputElement).value);
  }
}
