import { Component, OnInit, inject, input, output } from '@angular/core';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Mascota } from '../../models/mascota.model';
import { Especie } from '../../models/enums/especie.enum';
import { Sexo } from '../../models/enums/sexo.enum';
import { UploadFotoComponent } from '../upload-foto/upload-foto.component';

@Component({
  selector: 'app-formulario-mascota',
  imports: [ReactiveFormsModule, UploadFotoComponent],
  templateUrl: './formulario-mascota.component.html'
})
export class FormularioMascotaComponent implements OnInit {
  guardar = output<Mascota>();
  guardarYLimpiar = output<Mascota>();
  cancelar = output<void>();
  mascota = input<Mascota>();

  private route = inject(ActivatedRoute);

  esEdicion = false;
  id = 0;
  imagen = '';
  private original?: Mascota;

  mascotaForm = new FormGroup({
    nombre: new FormControl('', [
      Validators.required,
      Validators.minLength(3)
    ]),
    raza: new FormControl('', [
      Validators.required
    ]),
    edad: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0)
    ]),
    pesoKg: new FormControl<number | null>(null, [
      Validators.required,
      Validators.min(0)
    ]),
    especie: new FormControl<Especie | ''>('', [
      Validators.required
    ]),
    sexo: new FormControl<Sexo | ''>('', [
      Validators.required
    ]),
    color: new FormControl('', [
      Validators.required
    ]),
    fechaIngreso: new FormControl('', [
      Validators.required
    ])
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.params['id'];

    if (idParam) {
      this.esEdicion = true;
      this.id = Number(idParam);
    }

    const mascota = this.mascota();
    if (mascota) {
      this.original = mascota;
      this.mascotaForm.patchValue({
        nombre: mascota.nombre,
        raza: mascota.raza,
        edad: mascota.edad,
        pesoKg: mascota.pesoKg,
        especie: mascota.especie,
        sexo: mascota.sexo,
        color: mascota.color,
        fechaIngreso: mascota.fechaIngreso
      });
      this.imagen = mascota.imagen ?? '';
    }
  }

  onGuardar(): void {
    if (this.mascotaForm.invalid) return;
    this.guardar.emit(this.aMascota(this.id));
  }

  onGuardarYLimpiar(): void {
    if (this.mascotaForm.invalid) return;
    this.guardarYLimpiar.emit(this.aMascota(0));
    this.limpiar();
  }

  onCancelar(): void {
    this.cancelar.emit();
  }

  limpiar(): void {
    this.mascotaForm.reset();
    this.imagen = '';
    this.original = undefined;
  }

  private aMascota(id: number): Mascota {
    const formVal = this.mascotaForm.value;

    return {
      id,
      nombre: formVal.nombre ?? '',
      raza: formVal.raza ?? '',
      especie: formVal.especie || 'Perro',
      edad: Number(formVal.edad),
      sexo: formVal.sexo || 'Macho',
      imagen: this.imagen,
      pesoKg: Number(formVal.pesoKg),
      color: formVal.color ?? '',
      fechaIngreso: formVal.fechaIngreso ?? '',
      dueno: this.original?.dueno,
      estado: this.original?.estado ?? 'Activo'
    };
  }
}
