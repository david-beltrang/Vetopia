import { Component, input } from '@angular/core';

@Component({
  selector: 'app-kpi-card',
  templateUrl: './kpi-card.component.html'
})
// Representa una tarjeta reutilizable de indicador.
export class KpiCardComponent {
  title = input('');
  value = input('');
  icon = input('');
  iconBgClass = input('bg-[#e8faf2]');
  iconTextClass = input('text-[#006c52]');
  valueClass = input('text-[#191c1e]');
}
