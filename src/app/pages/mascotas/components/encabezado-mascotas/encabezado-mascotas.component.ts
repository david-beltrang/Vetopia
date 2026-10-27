import { Component, output } from '@angular/core';

@Component({
  selector: 'app-encabezado-mascotas',
  templateUrl: './encabezado-mascotas.component.html',
  styles: [':host { display: block; }']
})
export class EncabezadoMascotasComponent {
  // Comunica los criterios de búsqueda al componente que contiene la tabla.
  busqueda = output<string>();
  soloAtendidas = output<boolean>();

  actualizarBusqueda(event: Event): void {
    this.busqueda.emit((event.target as HTMLInputElement).value);
  }

  actualizarFiltro(event: Event): void {
    this.soloAtendidas.emit((event.target as HTMLInputElement).checked);
  }
}
