import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

@Component({
  selector: 'app-datos-mascota-cliente',
  templateUrl: './datos-mascota-cliente.component.html'
})
export class DatosMascotaClienteComponent {
  mascota = input.required<Mascota>();

  // Número de paciente de 4 dígitos: padStart(4, '0') rellena con ceros a la izquierda (7 -> "0007").
  // El ?? 0 cubre que el id sea opcional en el modelo.
  numeroPaciente(): string {
    return 'Paciente N° ' + String(this.mascota().id ?? 0).padStart(4, '0');
  }

  especie(): string {
    const especie = this.mascota().especie;
    return especie ? String(especie) : 'No registrada';
  }

  raza(): string {
    const raza = this.mascota().raza;
    return raza && raza.trim() ? raza : 'No registrada';
  }

  sexo(): string {
    const sexo = this.mascota().sexo;
    return sexo ? String(sexo) : 'No registrado';
  }

  edad(): string {
    return `${this.mascota().edad} años`;
  }

  peso(): string {
    const peso = this.mascota().pesoKg;
    return peso != null ? `${peso} kg` : 'No registrado';
  }

  estado(): string {
    const estado = this.mascota().estado;
    return estado ? String(estado) : 'No registrado';
  }
}
