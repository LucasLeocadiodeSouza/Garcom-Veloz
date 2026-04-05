import { inject, PLATFORM_ID } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { map, catchError, of } from 'rxjs';
import { isPlatformBrowser } from '@angular/common';
import { RequestForm } from '../service/request-form';
import { AlertService } from '../service/alert-service';

export const authGuard: CanActivateFn = (route, state) => {
  const request    = inject(RequestForm);
  const router     = inject(Router);
  const alertSvc   = inject(AlertService);
  const platformId = inject(PLATFORM_ID);

  // Em SSR não há token nem localStorage; deixa o Angular carregar normalmente.
  // A verificação real acontece no browser.
  if (!isPlatformBrowser(platformId)) {
    return true;
  }

  // Se não tem token no localStorage, redireciona direto para login
  const token = localStorage.getItem('token');
  if (!token) {
    return router.createUrlTree(['/login']);
  }

  const targetRoute = '/' + state.url.split('/').filter(Boolean)[0];

  return request.executeRequestGET('api/getTelasByPerfil').pipe(
    map((telas: { router: string; label: string }[]) => {
      const hasAccess = telas.some(t => t.router === targetRoute);

      if (hasAccess) {
        return true;
      }

      // Mostra o aviso e redireciona para /home via UrlTree
      // (o modal global no app.html exibe a mensagem enquanto o usuário já está em /home)
      alertSvc.show('Você não tem permissão para acessar esta tela.');
      return router.createUrlTree(['/home']);
    }),
    catchError((error) => {
      // Se o backend retorna 401 ou 403, o token expirou ou é inválido
      if (error?.status === 401 || error?.status === 403) {
        localStorage.removeItem('token');
        return of(router.createUrlTree(['/login']));
      }
      return of(router.createUrlTree(['/login']));
    })
  );
};
