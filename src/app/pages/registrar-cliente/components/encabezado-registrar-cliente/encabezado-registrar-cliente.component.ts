import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-registrar-cliente',
  templateUrl: './encabezado-registrar-cliente.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoRegistrarClienteComponent {
  titulo = input('Registrar Nuevo Cliente');
  descripcion = input('Completar los detalles para registrar el cliente y su mascota');
}
