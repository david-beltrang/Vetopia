import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';

export type SeccionAdmin = 'dashboard' | 'veterinarios';

@Component({
  selector: 'app-header-admin',
  templateUrl: './header-admin.component.html'
})
export class HeaderAdminComponent {
  adminId = input<number>(0);
  seccionActiva = input<SeccionAdmin>('dashboard');
  router = inject(Router);

  claseEnlace(seccion: SeccionAdmin): string {
    const base = 'font-body-md text-body-md transition-colors duration-200';
    return this.seccionActiva() === seccion
      ? `${base} text-[#7fffd4] font-bold border-b-2 border-[#7fffd4] pb-1 shadow-[0px_2px_4px_rgba(0,0,0,0.30)] hover:text-primary`
      : `${base} text-gray-300 hover:text-primary`;
  }

  navigateToDashboard(): void {
    this.router.navigate(['/admin/dashboard'], { queryParams: { adminId: this.adminId() } });
  }

  navigateToVeterinarios(): void {
    this.router.navigate(['/admin/veterinarios'], { queryParams: { adminId: this.adminId() } });
  }

  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }
}
