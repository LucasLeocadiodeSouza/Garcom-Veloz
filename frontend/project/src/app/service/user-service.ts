import { Injectable, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { RequestForm } from './request-form';

interface User{
  username: string,
  perfil:   string
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private user    = signal<{ username: string, perfil: string } | { username: "", perfil: ""}>({ username: "", perfil: ""});
  private request = inject(RequestForm);

  private platformId = inject(PLATFORM_ID);

  constructor() {
    this.fetchUser();
  }

  fetchUser() {
    if (isPlatformBrowser(this.platformId)) {
      const token = localStorage.getItem('token');
      if (!token) return;

      this.request.executeRequestGET('api/getInfoUser').subscribe({
        next: (response: {username: string, perfil: string}) => {
          this.user.set({
            username: response.username,
            perfil:   response.perfil
          });
        },
        error: (error) => console.error("Erro ao carregar informacoes do username: ", error)
      });
    }
  }


  setUser(data: User) {
    this.user.set(data);
  }

  getUser() {
    return this.user;
  }
}
