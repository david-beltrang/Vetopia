import { Component, input } from '@angular/core';

@Component({
  selector: 'app-upload-foto',
  templateUrl: './upload-foto.component.html'
})
export class UploadFotoComponent {
  imagen = input<string>();
  nombre = input<string>('');

  get tieneImagen(): boolean {
    return !!this.imagen();
  }
}