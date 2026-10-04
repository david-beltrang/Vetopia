import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Mascota } from '../../../../models/mascota.model';
import { UploadFotoComponent } from '../upload-foto/upload-foto.component';

@Component({
  selector: 'app-formulario-mascota',
  imports: [CommonModule, FormsModule, UploadFotoComponent],
  templateUrl: './formulario-mascota.component.html'
})
export class FormularioMascotaComponent {
  mascota = input<Mascota>();
  veterinarioId = input<number>();
  guardar = output<void>();
  cancelar = output<void>();

  onGuardar(): void {
    this.guardar.emit();
  }

  onCancelar(): void {
    this.cancelar.emit();
  }

  onFechaChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (this.mascota() && target.valueAsDate) {
      this.mascota()!.fechaIngreso = target.valueAsDate;
    }
  }
}
