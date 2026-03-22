import { Component, input, inject, signal, HostListener, ElementRef, Input } from '@angular/core';
import { RequestForm } from '../../service/request-form';
import { UserService } from '../../service/user-service';
@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
  standalone: true,
})
export class Topbar {
  pageTitle  = input<string>('Dashboard');
  elementRef = inject(ElementRef);

  private request     = inject(RequestForm);
  private userService = inject(UserService);

  user = this.userService.getUser();

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
    this.request.logout();
  }
}
