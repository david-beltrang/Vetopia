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
  ingresar = output<{ username: string; password: string }>();

  loginForm = new FormGroup({
    username: new FormControl('', [Validators.required]),
    password: new FormControl('', [Validators.required])
  });

  get usernameControl(): FormControl {
    return this.loginForm.get('username') as FormControl;
  }

  get passwordControl(): FormControl {
    return this.loginForm.get('password') as FormControl;
  }

  onIngresar(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    const { username, password } = this.loginForm.value;
    this.ingresar.emit({ username: username ?? '', password: password ?? '' });
  }
}
