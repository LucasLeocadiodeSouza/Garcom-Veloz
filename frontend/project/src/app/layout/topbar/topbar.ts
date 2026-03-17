import { Component, input, inject, signal, HostListener, ElementRef } from '@angular/core';
import { Router } from '@angular/router';
@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
  standalone: true,
})
export class Topbar {
  pageTitle = input<string>('Dashboard');
  router = inject(Router);
  elementRef = inject(ElementRef);

  dropdownOpen = signal(false);

  toggleDropdown() {
    this.dropdownOpen.update(v => !v);
  }

  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    if (!this.elementRef.nativeElement.contains(event.target)) {
      this.dropdownOpen.set(false);
    }
  }

  logout() {
    this.dropdownOpen.set(false);
    this.router.navigate(['/login']);
  }
}
