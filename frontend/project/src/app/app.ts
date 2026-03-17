import { Component, inject, signal } from '@angular/core';
import { RouterOutlet, Router } from '@angular/router';
import { Sidebar } from './layout/sidebar/sidebar';
import { AlertService } from './service/alert-service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Sidebar],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App { 
  alert = inject(AlertService);
  router = inject(Router);

  isLoginPage() {
    return this.router.url === '/login';
  }
}
