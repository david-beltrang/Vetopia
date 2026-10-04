import { Component, ViewChild, ElementRef } from '@angular/core';

interface TeamMember {
  name: string;
  specialty: string;
  imageUrl: string;
  imageAlt: string;
}

@Component({
  selector: 'app-team',
  templateUrl: './team.component.html'
})
export class TeamComponent {
  @ViewChild('carousel') carousel!: ElementRef<HTMLDivElement>;

  teamMembers: TeamMember[] = [
    {
      name: 'Dra. Laura Gómez',
      specialty: 'Medicina Veterinaria',
      imageUrl: 'https://static.wixstatic.com/media/18bd0e_362fbe37c0fa49b9ac549267aef40bc3~mv2.jpg/v1/fill/w_744,h_950,fp_0.64_0.38,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/IMG_2808.jpg',
      imageAlt: 'Dra. Laura Gómez'
    },
    {
      name: 'Dr. Carlos Ruiz',
      specialty: 'Veterinario Cirujano',
      imageUrl: 'https://img.magnific.com/psd-premium/veterinario-sonriente-sosteniendo-perro-pequeno_1269612-43094.jpg',
      imageAlt: 'Dr. Carlos Ruiz'
    },
    {
      name: 'Dra. Felipe Torres',
      specialty: 'Cardiología veterinaria',
      imageUrl: 'https://thumbs.dreamstime.com/b/veterinario-y-perro-9461018.jpg',
      imageAlt: 'Dra. Felipe Torres'
    },
    {
      name: 'Dr. Jorge Méndez',
      specialty: 'Dermatología veterinaria',
      imageUrl: 'https://st2.depositphotos.com/3662505/6206/i/450/depositphotos_62061453-stock-photo-veterinary.jpg',
      imageAlt: 'Dr. Jorge Méndez'
    },
    {
      name: 'Dra. Sofía Herrera',
      specialty: 'Nutrición veterinaria',
      imageUrl: 'https://img.magnific.com/fotos-premium/medico-veterinario-clinica-veterinaria_1368-127913.jpg',
      imageAlt: 'Dra. Sofía Herrera'
    }
  ];

  scrollPrev(): void {
    if (this.carousel) {
      this.carousel.nativeElement.scrollBy({ left: -this.carousel.nativeElement.clientWidth * 0.6, behavior: 'smooth' });
    }
  }

  scrollNext(): void {
    if (this.carousel) {
      this.carousel.nativeElement.scrollBy({ left: this.carousel.nativeElement.clientWidth * 0.6, behavior: 'smooth' });
    }
  }
}
