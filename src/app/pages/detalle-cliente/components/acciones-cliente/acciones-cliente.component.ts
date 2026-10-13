import { Component, output } from '@angular/core';

@Component({
  selector: 'app-acciones-cliente',
  templateUrl: './acciones-cliente.component.html',
  styles: [':host { display: block; }']
})
export class AccionesClienteComponent {
  eliminar = output<void>();
  editar = output<void>();
  agregarMascota = output<void>();
}
