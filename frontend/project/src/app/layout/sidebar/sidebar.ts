import { Component, signal, effect, inject, PLATFORM_ID, OnInit, HostListener } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { RequestForm } from '../../service/request-form';
import { UserService } from '../../service/user-service';

interface NavItem {
  label: string;
  route: string;
  icon: SafeHtml;
}

@Component({
  selector: 'app-sidebar',
  imports: [RouterLink],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar implements OnInit {
  private request = inject(RequestForm);
  private userService = inject(UserService);

  user = this.userService.getUser();

  collapsed          = signal(false);
  mobileOpen         = signal(false);
  isMobile           = signal(false);
  private platformId = inject(PLATFORM_ID);
  private sanitizer  = inject(DomSanitizer);

  mainNav:  NavItem[] = [];
  adminNav: NavItem[] = [];



  constructor() {
    effect(() => {
      if (isPlatformBrowser(this.platformId)) {
        document.documentElement.style.setProperty(
          '--sidebar-width',
          (this.collapsed() && !this.isMobile()) ? '72px' : '260px'
        );
      }
    });
  }

  ngOnInit(): void { 
    if (isPlatformBrowser(this.platformId)) {
      this.isMobile.set(window.innerWidth <= 768);
    }
    this.loadFormTelas(); 
  }

  @HostListener('window:resize')
  onResize() {
    if (isPlatformBrowser(this.platformId)) {
      const mobile = window.innerWidth <= 768;
      this.isMobile.set(mobile);
      if (!mobile && this.mobileOpen()) {
        this.mobileOpen.set(false);
      }
    }
  }

  loadFormTelas(): void {
    this.request.executeRequestGET('api/getTelasByPerfil').subscribe({
      next: (telas: any[]) => {
        const svg = (html: string): SafeHtml =>
          this.sanitizer.bypassSecurityTrustHtml(html);

        this.mainNav  = telas
          .filter(t => !t.admtype)
          .map(t => ({ label: t.label, route: t.router, icon: svg(t.svg ?? '') }));

        this.adminNav = telas
          .filter(t => t.admtype)
          .map(t => ({ label: t.label, route: t.router, icon: svg(t.svg ?? '') }));
      },
      error: (error) => {
        // Fallback to default nav on error
        //this.loadDefaultNav();

        console.error("Erro ao carregar as telas", error);
      }
    });
  }



  hasMainPages(){ return this.mainNav.length != 0 }
  hasAdmPages(){ return this.adminNav.length != 0 }

  // private loadDefaultNav(): void {
  //   const svg = (html: string): SafeHtml =>
  //     this.sanitizer.bypassSecurityTrustHtml(html);

  //   this.mainNav = [
  //     {
  //       label: 'Home',
  //       route: '/home',
  //       icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 01-1 1H4a1 1 0 01-1-1V9.5z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M9 21V12h6v9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`)
  //     },
  //     {
  //       label: 'Produtos',
  //       route: '/produtos',
  //       icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-14L4 7m8 4v10M4 7v10l8 4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>`)
  //     },
  //     {
  //       label: 'Pedidos',
  //       route: '/pedidos',
  //       icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" stroke-width="1.8"/><path d="M8 9h8m-8 4h5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`)
  //     },
  //     {
  //       label: 'Importar / Exportar',
  //       route: '/importar-exportar',
  //       icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>`)
  //     },
  //   ];

  //   this.adminNav = [
  //     {
  //       label: 'Usuários',
  //       route: '/usuarios',
  //       icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="1.8"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`)
  //     },
  //     {
  //       label: 'Categorias',
  //       route: '/categorias',
  //       icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg>`)
  //     },
  //   ];
  // }

  toggleCollapse() {
    if (isPlatformBrowser(this.platformId) && window.innerWidth <= 768) {
      this.mobileOpen.update(v => !v);
    } else {
      this.collapsed.update(v => !v);
    }
  }

  closeMobileMenu() {
    if (this.mobileOpen()) {
      this.mobileOpen.set(false);
    }
  }

  isActive(route: string): boolean {
    if (!isPlatformBrowser(this.platformId)) return false;
    return window.location.pathname === route || window.location.pathname.startsWith(route + '/');
  }

  logout() {
    this.request.logout();
  }
}
