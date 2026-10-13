import { Component, input } from '@angular/core';

@Component({
  selector: 'app-encabezado-tratamiento',
  templateUrl: './encabezado-tratamiento.component.html'
})
export class EncabezadoTratamientoComponent {
  titulo = input('Asignar Nuevo Tratamiento');
  subtitulo = input('Portal Veterinario / Registro Clínico');
}
