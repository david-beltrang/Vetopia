import { Component, input, output } from '@angular/core';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'tr[app-fila-cliente]',
  templateUrl: './fila-cliente.component.html'
})
export class FilaClienteComponent {
  cliente = input.required<Dueno>();
  verDetalle = output<Dueno>();

  iniciales(): string {
    const nombre = this.cliente().nombre ?? '';
    return nombre ? nombre.slice(0, 2).toUpperCase() : '';
  }
}
