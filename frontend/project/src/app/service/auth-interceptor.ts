import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

/**
 * Interceptor HTTP que captura respostas 401 (Unauthorized) do backend.
 * Quando o token JWT expira, o backend retorna 401.
 * Este interceptor limpa o token inválido e redireciona para o login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {

      if (error.status === 401 || error.status === 403) {

        const currentUrl  = router.url;
        const isAuthRoute = req.url.includes('api/validarAutenticacao');

        if (isAuthRoute && currentUrl !== '/login') {
          localStorage.removeItem('token');
          router.navigate(['/login']);
        }
      }
      return throwError(() => error);
    })
  );
};
