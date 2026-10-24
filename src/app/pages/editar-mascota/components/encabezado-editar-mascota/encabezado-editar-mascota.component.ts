import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-editar-mascota',
  templateUrl: './encabezado-editar-mascota.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoEditarMascotaComponent {
  titulo = input('Editar Mascota');
  descripcion = input('Modifica los detalles de la mascota');
}
