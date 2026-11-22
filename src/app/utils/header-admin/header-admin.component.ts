import { Component, input, inject } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

export type SeccionAdmin = 'dashboard' | 'veterinarios' | 'citas';

@Component({
  selector: 'app-header-admin',
  templateUrl: './header-admin.component.html'
})
export class HeaderAdminComponent {
  adminId = input<number>(0);
  seccionActiva = input<SeccionAdmin>('dashboard');
  router = inject(Router);
  private auth = inject(AuthService);

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

  navigateToCitas(): void {
    this.router.navigate(['/admin/citas'], { queryParams: { adminId: this.adminId() } });
  }

  navigateToLogin(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}
