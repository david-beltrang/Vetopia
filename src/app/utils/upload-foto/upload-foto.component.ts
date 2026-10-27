import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-upload-foto',
  templateUrl: './upload-foto.component.html'
})
export class UploadFotoComponent {
  imagen = input<string>();
  nombre = input<string>('');
  imagenChange = output<string>();

  get tieneImagen(): boolean {
    return !!this.imagen();
  }

  onFileSelected(event: Event): void {
    // Convierte la imagen a una cadena para enviarla dentro del formulario.
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      const file = input.files[0];
      const reader = new FileReader();
      reader.onload = () => {
        const result = reader.result as string;
        this.imagenChange.emit(result);
      };
      reader.readAsDataURL(file);
    }
  }
}
