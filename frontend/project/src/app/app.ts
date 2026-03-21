import { Component, inject } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { Sidebar } from './layout/sidebar/sidebar';
import { AlertService } from './service/alert-service';
import { RequestForm } from './service/request-form';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  alert   = inject(AlertService);
  router  = inject(Router);
  request = inject(RequestForm);

  ngOnInit(){ this.validarAutenticacao() }

  validarAutenticacao() {
    this.request.executeRequestGET('api/validarAutenticacao').subscribe({
      next: (response: boolean) => {
        if(!response) this.router.navigate(['/login']);
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao validar o usuário. Por favor, recarregue a pagina.');
      }
    });
  }

  isLoginPage() {
    return this.router.url === '/login';
  }
}
