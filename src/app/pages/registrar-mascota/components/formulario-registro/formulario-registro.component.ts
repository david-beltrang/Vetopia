import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Mascota } from '../../../../models/mascota.model';
import { UploadFotoComponent } from '../upload-foto/upload-foto.component';

@Component({
  selector: 'app-formulario-registro',
  imports: [CommonModule, FormsModule, UploadFotoComponent],
  templateUrl: './formulario-registro.component.html'
})
export class FormularioRegistroComponent {
  guardar = output<Mascota>();
  guardarYLimpiar = output<Mascota>();

  mascota: Mascota = {
    id: 0,
    nombre: '',
    especie: 'Perro',
    raza: '',
    edad: 0,
    sexo: 'Macho',
    imagen: '',
    pesoKg: 0,
    color: '',
    fechaIngreso: new Date(),
    dueno: null as any,
    estado: 'Activo',
    tratamientos: [],
  };

  onGuardar(): void {
    this.guardar.emit(this.mascota);
  }

  onGuardarYLimpiar(): void {
    this.guardarYLimpiar.emit(this.mascota);
    this.limpiarFormulario();
  }

  onFechaChange(event: Event): void {
    const target = event.target as HTMLInputElement;
    if (target.valueAsDate) {
      this.mascota.fechaIngreso = target.valueAsDate;
    }
  }

  private limpiarFormulario(): void {
    this.mascota = {
      id: 0,
      nombre: '',
      especie: 'Perro',
      raza: '',
      edad: 0,
      sexo: 'Macho',
      imagen: '',
      pesoKg: 0,
      color: '',
      fechaIngreso: new Date(),
      dueno: null as any,
      estado: 'Activo',
      tratamientos: [],
    };
  }
}
