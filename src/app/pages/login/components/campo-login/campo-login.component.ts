import { Component, input } from '@angular/core';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

@Component({
  selector: 'app-campo-login',
  imports: [ReactiveFormsModule],
  templateUrl: './campo-login.component.html'
})
export class CampoLoginComponent {
  inputId = input.required<string>();
  label = input.required<string>();
  placeholder = input('');
  icono = input.required<string>();
  tipo = input<'text' | 'password'>('text');
  control = input.required<FormControl>();

  mostrar = false;

  tipoFinal(): string {
    if (this.tipo() === 'password' && this.mostrar) {
      return 'text';
    }
    return this.tipo();
  }

  toggleMostrar(): void {
    this.mostrar = !this.mostrar;
  }
}
