import { Component, input } from '@angular/core';

@Component({
  selector: 'app-tarjeta-info',
  templateUrl: './tarjeta-info.component.html',
  styles: [':host { display: block; }']
})
export class TarjetaInfoComponent {
  etiqueta = input.required<string>();
  valor = input.required<string>();
  detalle = input('');
  icono = input.required<string>();
  forma = input<'pill' | 'card'>('card');
}
