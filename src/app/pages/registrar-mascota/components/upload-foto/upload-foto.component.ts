import { Component, input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-upload-foto',
  imports: [CommonModule],
  templateUrl: './upload-foto.component.html'
})
export class UploadFotoComponent {
  imagen = input<string | null>(null);
  nombre = input<string>('');

  get tieneImagen(): boolean {
    return !!this.imagen();
  }
}
