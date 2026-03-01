import { Component, input } from '@angular/core';

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.html',
  styleUrl: './topbar.css',
  standalone: true,
})
export class Topbar {
  pageTitle = input<string>('Dashboard');
}
