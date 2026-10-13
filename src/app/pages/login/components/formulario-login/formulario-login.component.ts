import { Component, input, output } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { CampoLoginComponent } from '../campo-login/campo-login.component';

@Component({
  selector: 'app-formulario-login',
  imports: [ReactiveFormsModule, RouterLink, CampoLoginComponent],
  templateUrl: './formulario-login.component.html'
})
export class FormularioLoginComponent {
  mensajeError = input<string>('');
  ingresar = output<{ correo: string; contrasena: string }>();

  loginForm = new FormGroup({
    correo: new FormControl('', [Validators.required]),
    contrasena: new FormControl('', [Validators.required])
  });

  get correoControl(): FormControl {
    return this.loginForm.get('correo') as FormControl;
  }

  get contrasenaControl(): FormControl {
    return this.loginForm.get('contrasena') as FormControl;
  }

  onIngresar(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { correo, contrasena } = this.loginForm.value;
    this.ingresar.emit({ correo: correo ?? '', contrasena: contrasena ?? '' });
  }
}
