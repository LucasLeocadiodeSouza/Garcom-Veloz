import { Component } from '@angular/core';

@Component({
  selector: 'app-navigation-cards',
  imports: [],
  templateUrl: './navigation-cards.html',
  styleUrl: './navigation-cards.css',
})
export class NavigationCards {
  quickNavCards = [
    {
      title: 'Produtos',
      description: 'Visualize e gerencie todos os produtos cadastrados no sistema.',
      route: '/produtos',
      iconBg: 'linear-gradient(135deg, #3b82f6, #2563eb)',
    },
    {
      title: 'Importar / Exportar',
      description: 'Importe ou exporte planilhas Excel com os dados dos produtos.',
      route: '/importar-exportar',
      iconBg: 'linear-gradient(135deg, #10b981, #059669)',
    },
    {
      title: 'Usuários',
      description: 'Gerencie os usuários e permissões de acesso ao sistema.',
      route: '/usuarios',
      iconBg: 'linear-gradient(135deg, #8b5cf6, #7c3aed)',
    },
  ];
}
