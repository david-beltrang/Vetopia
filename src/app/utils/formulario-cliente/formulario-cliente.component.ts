import { Component, OnInit, inject, input, output } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Dueno } from '../../models/dueno.model';

@Component({
  selector: 'app-formulario-cliente',
  imports: [ReactiveFormsModule],
  templateUrl: './formulario-cliente.component.html'
})
export class FormularioClienteComponent implements OnInit {
  guardar = output<Dueno>();
  cancelar = output<void>();
  cliente = input<Dueno>();

  private route = inject(ActivatedRoute);

  esEdicion = false;
  id = 0;
  private original?: Dueno;

  clienteForm = new FormGroup({
    cedula: new FormControl('', [
      Validators.required,
      Validators.pattern('^[0-9]+$')
    ]),
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    correo: new FormControl('', [
      Validators.required,
      Validators.email
    ]),
    celular: new FormControl('', [
      Validators.required
    ]),
    contrasena: new FormControl('', [
      Validators.required,
      Validators.minLength(4)
    ])
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.params['id'];

    if (idParam) {
      this.esEdicion = true;
      this.id = Number(idParam);
    }

    const cliente = this.cliente();
    if (cliente) {
      this.original = cliente;
      this.clienteForm.patchValue({
        cedula: cliente.cedula,
        nombre: cliente.nombre,
        correo: cliente.correo,
        celular: cliente.celular,
        contrasena: cliente.contrasena
      });
    }
  }

  onGuardar(): void {
    if (this.clienteForm.invalid) return;

    const formVal = this.clienteForm.value;
    const dueno: Dueno = {
      id: this.id,
      cedula: formVal.cedula ?? '',
      nombre: formVal.nombre ?? '',
      correo: formVal.correo ?? '',
      celular: formVal.celular ?? '',
      contrasena: formVal.contrasena ?? ''
    };

    this.guardar.emit(dueno);
  }

  onCancelar(): void {
    this.cancelar.emit();
  }
}