import { Component, signal, effect, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

interface NavItem {
  label: string;
  route: string;
  icon: SafeHtml;
}

@Component({
  selector: 'app-sidebar',
  imports: [],
  templateUrl: './sidebar.html',
  styleUrl: './sidebar.css'
})
export class Sidebar {
  collapsed = signal(false);
  private platformId = inject(PLATFORM_ID);
  private sanitizer = inject(DomSanitizer);

  mainNav: NavItem[];
  adminNav: NavItem[];

  constructor() {
    const svg = (html: string): SafeHtml =>
      this.sanitizer.bypassSecurityTrustHtml(html);

    this.mainNav = [
      {
        label: 'Home',
        route: '/home',
        icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M3 9.5L12 3l9 6.5V20a1 1 0 01-1 1H4a1 1 0 01-1-1V9.5z" stroke="currentColor" stroke-width="1.8" stroke-linejoin="round"/><path d="M9 21V12h6v9" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`)
      },
      {
        label: 'Produtos',
        route: '/produtos',
        icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-14L4 7m8 4v10M4 7v10l8 4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>`)
      },
      {
        label: 'Pedidos',
        route: '/pedidos',
        icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><rect x="3" y="4" width="18" height="16" rx="2" stroke="currentColor" stroke-width="1.8"/><path d="M8 9h8m-8 4h5" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`)
      },
      {
        label: 'Importar / Exportar',
        route: '/importar-exportar',
        icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/></svg>`)
      },
    ];

    this.adminNav = [
      {
        label: 'Usuários',
        route: '/usuarios',
        icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/><circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="1.8"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75" stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/></svg>`)
      },
      {
        label: 'Categorias',
        route: '/categorias',
        icon: svg(`<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M20.59 13.41l-7.17 7.17a2 2 0 01-2.83 0L2 12V2h10l8.59 8.59a2 2 0 010 2.82z" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/><circle cx="7" cy="7" r="1.5" fill="currentColor"/></svg>`)
      },
    ];

    effect(() => {
      if (isPlatformBrowser(this.platformId)) {
        document.documentElement.style.setProperty(
          '--sidebar-width',
          this.collapsed() ? '72px' : '260px'
        );
      }
    });
  }

  toggleCollapse() {
    this.collapsed.update(v => !v);
  }

  isActive(route: string): boolean {
    if (!isPlatformBrowser(this.platformId)) return false;
    return window.location.pathname === route ||
      window.location.pathname.startsWith(route + '/');
  }
}
