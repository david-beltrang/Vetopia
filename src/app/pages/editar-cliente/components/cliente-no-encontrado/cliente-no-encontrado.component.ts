import { Component, output } from '@angular/core';

@Component({
  selector: 'app-cliente-no-encontrado',
  templateUrl: './cliente-no-encontrado.component.html',
  styles: [':host { display: block; }']
})
export class ClienteNoEncontradoComponent {
  volver = output<void>();
}
