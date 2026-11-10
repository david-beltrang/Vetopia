import { Component, inject } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { UBICACION_VETOPIA } from '../../../../config/ubicacion-vetopia';

@Component({
  selector: 'app-location',
  templateUrl: './location.component.html'
})
export class LocationComponent {
  readonly ubicacion = UBICACION_VETOPIA;
  readonly mapaEmbedUrl: SafeResourceUrl = inject(DomSanitizer)
    .bypassSecurityTrustResourceUrl(UBICACION_VETOPIA.urlMapaEmbed);
}
