import { Component, input, output } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Veterinario } from '../../../../models/veterinario.model';
import { Mascota } from '../../../../models/mascota.model';
import { Dueno } from '../../../../models/dueno.model';
import { Droga } from '../../../../models/droga.model';
import { TarjetaInfoComponent } from '../tarjeta-info/tarjeta-info.component';

export interface TratamientoFormValue {
  fecha: string;
  drogaId: number;
  titulo: string;
  descripcion: string;
}

function fechaHoy(): string {
  const d = new Date();
  const pad = (n: number) => n.toString().padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

@Component({
  selector: 'app-formulario-tratamiento',
  imports: [ReactiveFormsModule, TarjetaInfoComponent],
  templateUrl: './formulario-tratamiento.component.html'
})
export class FormularioTratamientoComponent {
  veterinario = input<Veterinario>();
  mascota = input<Mascota>();
  dueno = input<Dueno>();
  drogas = input<Droga[]>([]);

  guardar = output<TratamientoFormValue>();
  cancelar = output<void>();

  form = new FormGroup({
    fecha: new FormControl(fechaHoy(), { nonNullable: true, validators: [Validators.required] }),
    drogaId: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    titulo: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    descripcion: new FormControl('', { nonNullable: true, validators: [Validators.required] })
  });

  detalleMascota(): string {
    const mascota = this.mascota();
    if (!mascota) return '';
    return `${mascota.especie} / ${mascota.raza}`;
  }

  onGuardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const valor = this.form.getRawValue();
    this.guardar.emit({
      fecha: valor.fecha,
      drogaId: Number(valor.drogaId),
      titulo: valor.titulo,
      descripcion: valor.descripcion
    });
  }

  onCancelar(): void {
    this.cancelar.emit();
  }
}
