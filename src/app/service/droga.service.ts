import { Injectable } from '@angular/core';
import { Droga } from '../models/droga.model';

@Injectable({
  providedIn: 'root',
})
export class DrogaService {
  private drogas: Droga[] = [
    { id: 1, nombre: 'Amoxicilina', precioCompra: 5000, precioVenta: 8000, unidadesDisponibles: 100, unidadesVendidas: 50, tratamientos: [] },
    { id: 2, nombre: 'Ivermectina', precioCompra: 3000, precioVenta: 5000, unidadesDisponibles: 80, unidadesVendidas: 30, tratamientos: [] },
    { id: 3, nombre: 'Antiparasitario', precioCompra: 4000, precioVenta: 6500, unidadesDisponibles: 60, unidadesVendidas: 25, tratamientos: [] },
  ];

  getDrogas(): Droga[] {
    return this.drogas.slice();
  }

  getDrogaById(id: number): Droga | undefined {
    return this.drogas.find((droga) => droga.id === id);
  }
}
