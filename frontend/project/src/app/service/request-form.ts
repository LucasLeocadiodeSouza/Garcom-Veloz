import { HttpClient } from '@angular/common/http';
import { inject, Injectable, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { Router } from '@angular/router';
import { catchError, map, Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RequestForm {
  private apiUrl = 'http://localhost:8080';
  private platformId = inject(PLATFORM_ID);

  constructor(private http: HttpClient) {}

  router = inject(Router);

  executeRequestGET(path: string, params?: any): Observable<any> {
      let url = `${this.apiUrl}/${path}`;
      let token = '';

      if (isPlatformBrowser(this.platformId)) token = localStorage.getItem('token') || '';

      if (params) {
          const queryParams = new URLSearchParams(params).toString();
          url += '?' + queryParams;
      }

      return this.http.get(url, {withCredentials: true, headers: {
                                                                   Authorization: `Bearer ${token}`
                                                                 }
                                });
  }

  executeRequestPOST(path: string, body: any, params?: any): Observable<any> {
    let url = `${this.apiUrl}/${path}`;

    if (params) {
      const queryParams = new URLSearchParams(params).toString();
      url += '?' + queryParams;
    }

    let token = '';
    if (isPlatformBrowser(this.platformId)) token = localStorage.getItem('token') || '';

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
    if (isPlatformBrowser(this.platformId)) localStorage.removeItem('token');
    this.router.navigate(['/login']);
  }
}
