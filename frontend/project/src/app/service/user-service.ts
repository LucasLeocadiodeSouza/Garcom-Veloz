import { Injectable, signal } from '@angular/core';

interface User{
  username: string,
  perfil:   string
}

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private user = signal<{ username: string, perfil: string } | null>(null);

  setUser(data: User) {
    this.user.set(data);
  }

  getUser() {
    return this.user;
  }
}
