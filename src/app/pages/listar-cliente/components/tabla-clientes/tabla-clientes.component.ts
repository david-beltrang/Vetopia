import { Component, input, output } from '@angular/core';
import { Dueno } from '../../../../models/dueno.model';
import { FilaClienteComponent } from '../fila-cliente/fila-cliente.component';

@Component({
  selector: 'app-tabla-clientes',
  imports: [FilaClienteComponent],
  templateUrl: './tabla-clientes.component.html'
})
export class TablaClientesComponent {
  clientes = input<Dueno[]>([]);
  textoContador = input('');
  verDetalle = output<Dueno>();
}
