import { Component, input, output } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Veterinario } from '../../../../models/veterinario.model';
import { UploadFotoComponent } from '../../../../utils/upload-foto/upload-foto.component';

@Component({
  selector: 'app-formulario-registrar-veterinario',
  imports: [ReactiveFormsModule, UploadFotoComponent],
  templateUrl: './formulario-registrar-veterinario.component.html'
})
// Valida y emite los datos del nuevo veterinario.
export class FormularioRegistrarVeterinarioComponent {
  guardar = output<Veterinario>();
  cancelar = output<void>();
  veterinario = input<Veterinario>();

  imagen = '';

  veterinarioForm = new FormGroup({
    nombre: new FormControl('', [Validators.required, Validators.minLength(3)]),
    cedula: new FormControl('', [Validators.required, Validators.pattern('^[0-9]+$')]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    contrasena: new FormControl('', [Validators.required, Validators.minLength(4)]),
    especialidad: new FormControl('', [Validators.required]),
    estado: new FormControl<'Activo' | 'Inactivo'>('Activo', [Validators.required])
  });

  onImagenChange(imagen: string): void {
    this.imagen = imagen;
  }

  // Evita enviar datos incompletos y construye el objeto esperado por la API.
  onGuardar(): void {
    if (this.veterinarioForm.invalid) {
      this.veterinarioForm.markAllAsTouched();
      return;
    }

    const formVal = this.veterinarioForm.value;
    const veterinario: Veterinario = {
      cedula: formVal.cedula ?? '',
      contrasena: formVal.contrasena ?? '',
      correo: formVal.correo ?? '',
      especialidad: formVal.especialidad ?? '',
      numeroAtenciones: 0,
      nombre: formVal.nombre ?? '',
      estado: formVal.estado ?? 'Activo',
      urlFoto: this.imagen || undefined
    };

    this.guardar.emit(veterinario);
  }

  onCancelar(): void {
    this.cancelar.emit();
  }
}
