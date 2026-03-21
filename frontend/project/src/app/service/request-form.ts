import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RequestForm {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  router = inject(Router);

  // getProdutos(): Observable<any[]> {
  //   return this.http.get<any[]>(`${this.apiUrl}/produtos`);
  // }

  // // POST - Criar dados
  // criarProduto(produto: any): Observable<any> {
  //   return this.http.post(`${this.apiUrl}/produtos`, produto);
  // }

  executeRequestGET(path: string, params?: any): Observable<any> {
      let url = `${this.apiUrl}/${path}`;

      const token = localStorage.getItem('token');

      // Adiciona query parameters se existirem
      if (params) {
          const queryParams = new URLSearchParams(params).toString();
          url += '?' + queryParams;
      }

      return this.http.get(url, {withCredentials: true, headers: {
                                                                   Authorization: `Bearer ${token}`
                                                                 }
                                });
  }

  executeRequestPOST(path: string, body: any, params?: any, ignoreAuteTran?: boolean): Observable<any> {
    let url = `${this.apiUrl}/${path}`;

    // Adiciona query parameters se existirem
    if (params) {
      const queryParams = new URLSearchParams(params).toString();
      url += '?' + queryParams;
    }

    const token = localStorage.getItem('token');

    if (!token && path !== 'auth/login') {
      this.http.get(`${this.apiUrl}/auth/me`, { withCredentials: true }).subscribe({
        next: () => {
          return this.http.post(url, body, {withCredentials: true});
        },
        error: (error) => this.router.navigate(['/login'])
      });
    }

    return this.http.post(url, body, {withCredentials: true, headers: {
                                                                        Authorization: `Bearer ${token}`
                                                                      }
                                     });
  }

  isLoggedIn(): Observable<boolean> {
    return this.http.get(`${this.apiUrl}/auth/me`, { withCredentials: true }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  logout() {
    localStorage.setItem('token', "");
    this.router.navigate(['/login']);
    //this.http.post(`${this.apiUrl}/auth/logout`, { withCredentials: true });
  }
}
