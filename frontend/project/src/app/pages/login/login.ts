import { Response } from 'express';
import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertService } from '../../service/alert-service';
import { RequestForm } from '../../service/request-form';
import { UserService } from '../../service/user-service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css'
})
export class Login {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);
  private user    = inject(UserService);
  private router  = inject(Router);

  formLogin:   string = '';
  formPasskey: string = '';

  onLogin(event: Event) {
    event.preventDefault();
    this.authentication();
  }

  authentication(){
    this.request.executeRequestPOST('auth/login', { login: this.formLogin, passkey: this.formPasskey }, {}, true).subscribe({
      next: (response) => {
        localStorage.setItem('token', response.token);

        this.loadUserInfo();

        this.router.navigate(['/home']);
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Usuário ou senha incorretos. Tente novamente ou clique em "Esqueci a senha".');
      }
    });
  }

  loadUserInfo(){
    this.user.fetchUser();
  }
}
