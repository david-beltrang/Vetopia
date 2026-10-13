import { Component, input } from '@angular/core';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'app-datos-contacto-cliente',
  templateUrl: './datos-contacto-cliente.component.html',
  styles: [':host { display: block; }']
})
export class DatosContactoClienteComponent {
  cliente = input.required<Dueno>();
}
