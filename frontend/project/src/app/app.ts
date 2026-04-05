import { Component, inject, PLATFORM_ID } from '@angular/core';
import { RouterOutlet, Router, NavigationEnd } from '@angular/router';
import { isPlatformBrowser } from '@angular/common';
import { filter } from 'rxjs';
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
    if (this.isPublicPage()) return;

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

  isPublicPage() {
    const url = this.router.url;

    // O router inicia no '/' antes de redirecionar para o '/login' na configuração inicial.
    // Ignorá-lo evita que o Sidebar seja renderizado por uma fração de segundo e faça as consultas na API sem token.

    return url === '/' || url === '' || url === '/login' || url.startsWith('/cardapio');
  }
}
