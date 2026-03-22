import { Component, input, inject, signal, HostListener, ElementRef } from '@angular/core';
import { RequestForm } from '../../service/request-form';
@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
  standalone: true,
})
export class Topbar {
  pageTitle  = input<string>('Dashboard');
  elementRef = inject(ElementRef);

  private request = inject(RequestForm);

  username: string = "";
  perfil: string = "";

  dropdownOpen = signal(false);

  ngOnInit(){ this.getUsername() }

  toggleDropdown() {
    this.dropdownOpen.update(v => !v);
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.dropdownOpen.set(false);
    }
  }

  getUsername(): void {
    this.request.executeRequestGET('api/getUsername').subscribe({
      next: (response: {username: string}) => {
        this.username = response.username;
        this.getPerfilUsuario();
      },
      error: (error) => console.error("Erro ao carregar o username: ", error)
    });
  }

  getPerfilUsuario(): void {
    this.request.executeRequestGET('api/getPerfilUsuario').subscribe({
      next: (response: {perfil: string}) => this.perfil = response.perfil,
      error: (error) => console.error("Erro ao carregar o perfil do usuário", error)
    });
  }

  logout() {
    this.dropdownOpen.set(false);
    this.request.logout();
  }
}
