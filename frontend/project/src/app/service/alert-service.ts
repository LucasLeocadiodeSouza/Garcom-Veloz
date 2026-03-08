import { Injectable, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class AlertService {
  showAlertMessage = signal<string | null>(null);

  show(message: string) {
    this.showAlertMessage.set(message);
  }

  close() {
    this.showAlertMessage.set(null);
  }

}
