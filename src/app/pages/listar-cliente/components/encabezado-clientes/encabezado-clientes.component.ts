import { Component, output } from '@angular/core';

@Component({
  selector: 'app-encabezado-clientes',
  templateUrl: './encabezado-clientes.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoClientesComponent {
  nuevoCliente = output<void>();
}
