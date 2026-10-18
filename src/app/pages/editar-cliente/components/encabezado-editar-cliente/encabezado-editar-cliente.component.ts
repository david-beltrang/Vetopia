import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-editar-cliente',
  templateUrl: './encabezado-editar-cliente.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoEditarClienteComponent {
  titulo = input('Editar Cliente');
  descripcion = input('Modifica los detalles del cliente');
}
