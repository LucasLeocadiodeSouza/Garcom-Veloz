import { Component } from '@angular/core';

@Component({
  selector: 'app-recent-activity',
  imports: [],
  templateUrl: './recent-activity.html',
  styleUrl: './recent-activity.css',
})
export class RecentActivity {
  recentActivity = [
    { text: 'Produto "Hambúrguer Artesanal" cadastrado', time: 'há 5 min', color: '#3b82f6' },
    { text: 'Planilha exportada com 248 produtos', time: 'há 22 min', color: '#10b981' },
    { text: 'Usuário João Silva adicionado', time: 'há 1h', color: '#8b5cf6' },
    { text: 'Produto "Suco de Laranja" atualizado', time: 'há 2h', color: '#f59e0b' },
    { text: 'Importação de 12 novos produtos', time: 'ontem', color: '#06b6d4' },
  ];
}
