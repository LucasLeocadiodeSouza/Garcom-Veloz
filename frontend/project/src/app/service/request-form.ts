import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RequestForm {
  private apiUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) {}

  // getProdutos(): Observable<any[]> {
  //   return this.http.get<any[]>(`${this.apiUrl}/produtos`);
  // }

  // // POST - Criar dados
  // criarProduto(produto: any): Observable<any> {
  //   return this.http.post(`${this.apiUrl}/produtos`, produto);
  // }

  executeRequestGET(path: string, params?: any): Observable<any> {
      let url = `${this.apiUrl}/${path}`;

      // Adiciona query parameters se existirem
      if (params) {
          const queryParams = new URLSearchParams(params).toString();
          url += '?' + queryParams;
      }

      return this.http.get(url, {withCredentials: true});
  }

  executeRequestPOST(path: string, body: any, params?: any): Observable<any> {
    let url = `${this.apiUrl}/${path}`;

    // Adiciona query parameters se existirem
    if (params) {
        const queryParams = new URLSearchParams(params).toString();
        url += '?' + queryParams;
    }

    return this.http.post(url, body, {withCredentials: true});
  }

  isLoggedIn(): Observable<boolean> {
    return this.http.get(`${this.apiUrl}/auth/me`, { withCredentials: true }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}
