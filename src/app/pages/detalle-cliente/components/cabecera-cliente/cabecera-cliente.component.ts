import { Component, input } from '@angular/core';

@Component({
  selector: 'app-cabecera-cliente',
  templateUrl: './cabecera-cliente.component.html',
  styles: [':host { display: block; }']
})
export class CabeceraClienteComponent {
  nombre = input('');
  id = input(0);

  iniciales(): string {
    const nombre = this.nombre();
    return nombre ? nombre.slice(0, 2).toUpperCase() : '';
  }
}
