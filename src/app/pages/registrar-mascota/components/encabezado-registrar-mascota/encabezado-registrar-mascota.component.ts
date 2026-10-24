import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-registrar-mascota',
  templateUrl: './encabezado-registrar-mascota.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoRegistrarMascotaComponent {
  titulo = input('Registrar Nueva Mascota');
  descripcion = input('Completar los detalles de la mascota para el cliente');
}
