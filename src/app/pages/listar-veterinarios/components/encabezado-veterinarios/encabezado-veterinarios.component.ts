import { Component, output } from '@angular/core';

@Component({
  selector: 'app-encabezado-veterinarios',
  templateUrl: './encabezado-veterinarios.component.html',
  styles: [':host { display: block; }']
})
// Contiene el título, buscador y acción de registro.
export class EncabezadoVeterinariosComponent {
  // Eventos que delegan la búsqueda y el registro al componente padre.
  nuevoVeterinario = output<void>();
  busqueda = output<string>();

  // Actualiza la busqueda de la barra
  actualizarBusqueda(event: Event): void {
    this.busqueda.emit((event.target as HTMLInputElement).value);
  }
}
