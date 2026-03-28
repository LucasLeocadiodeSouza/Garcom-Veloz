import { Component, inject } from '@angular/core';
import { RequestForm } from '../../service/request-form';
import { Time } from '@angular/common';

@Component({
  selector: 'app-recent-activity',
  imports: [],
  templateUrl: './recent-activity.html',
  styleUrl: './recent-activity.css',
})
export class RecentActivity {
  private request = inject(RequestForm);

  recentActivity: { id:    number,
                    text:  string,
                    time:  string,
                    color: string }[] = [];

  themes = [
      '#06b6d4',
      '#f59e0b',
      '#8b5cf6',
      '#10b981',
      '#3b82f6'
    ];

  ngOnInit(){
    this.getRecentHistoricoByUsuario();
  }

  getRecentHistoricoByUsuario(){
    this.request.executeRequestGET('restrictedApi/getRecentHistoricoByUsuario').subscribe({
      next: (response: any[]) => {

        this.recentActivity = response.map((hist: any) => ({
          id:     hist.id,
          text:   hist.label,
          time:   this.formatRelativeTime(hist.criadoEm, hist.horario),
          color:  this.getRandomTheme()
        }));
      },
      error: (error) => {
        console.error('Erro:', error);
      }
    });
  }

  formatRelativeTime(dataProp: any, horarioProp: any): string {
    if (!dataProp) return '';

    let activityDate: Date;

    if (Array.isArray(dataProp)) {
      const year = dataProp[0];
      const month = dataProp[1] - 1;
      const day = dataProp[2];

      let hour = 0, minute = 0, second = 0;
      if (Array.isArray(horarioProp)) {
        hour = horarioProp[0] || 0;
        minute = horarioProp[1] || 0;
        second = horarioProp[2] || 0;
      } else if (typeof horarioProp === 'string') {
        const parts = horarioProp.split(':');
        hour = parseInt(parts[0] || '0', 10);
        minute = parseInt(parts[1] || '0', 10);
        second = parseInt(parts[2] || '0', 10);
      }

      activityDate = new Date(year, month, day, hour, minute, second);
    } else {
      const datePart = typeof dataProp === 'string' ? dataProp.split('T')[0] : String(dataProp);
      let timePart = "00:00:00";

      if (typeof horarioProp === 'string') {
        timePart = horarioProp.length <= 5 ? `${horarioProp}:00` : horarioProp;
      }

      activityDate = new Date(`${datePart}T${timePart}`);
      if (isNaN(activityDate.getTime())) {
        activityDate = new Date(dataProp);
      }
    }

    if (isNaN(activityDate.getTime())) return String(dataProp).split('T')[0] || '';

    const now = new Date();
    const diffMs = now.getTime() - activityDate.getTime();

    const diffMinutes = Math.floor(diffMs / (1000 * 60));
    const diffHours = Math.floor(diffMs / (1000 * 60 * 60));

    const today = new Date(now.getFullYear(), now.getMonth(), now.getDate());
    const activityDay = new Date(activityDate.getFullYear(), activityDate.getMonth(), activityDate.getDate());
    const diffDays = Math.floor((today.getTime() - activityDay.getTime()) / (1000 * 60 * 60 * 24));

    if (diffMs < 0 || diffMinutes < 1) return 'Agora';
    if (diffMinutes < 60) return `${diffMinutes} min`;
    if (diffDays === 0) return `${diffHours} hora${diffHours > 1 ? 's' : ''} atrás`;
    if (diffDays === 1) return 'Ontem';
    if (diffDays <= 30) return `${diffDays} dias atrás`;

    return '30+ dias atrás';
  }

  getRandomTheme(): string {
    const randomIndex = Math.floor(Math.random() * this.themes.length);
    return this.themes[randomIndex];
  }
}
