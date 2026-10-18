import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-mascotas',
  templateUrl: './encabezado-mascotas.component.html'
})
export class EncabezadoMascotasComponent {
  titulo = input('Mis Mascotas');
  descripcion = input('Consulta la información de tus mascotas registradas en Vetopia. Selecciona en Ver Detalles para visualizar su perfil completo.');
}
