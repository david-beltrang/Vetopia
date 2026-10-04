import { Component, input } from '@angular/core';
import { TratamientoDetalle } from '../../../../models/ficha-clinica.model';

@Component({
  selector: 'app-item-tratamiento',
  templateUrl: './item-tratamiento.component.html'
})
export class ItemTratamientoComponent {
  item = input<TratamientoDetalle>();

  get esCompletado(): boolean {
    return this.item()?.tratamiento?.completado ?? false;
  }

  get fechaFormateada(): string {
    if (!this.item()?.tratamiento?.fecha) return '';
    const fecha = new Date(this.item()!.tratamiento.fecha);
    return fecha.toLocaleDateString('es-ES', { day: 'numeric', month: 'short', year: 'numeric' });
  }
}
