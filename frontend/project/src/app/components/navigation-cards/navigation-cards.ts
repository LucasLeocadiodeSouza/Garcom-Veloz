import { Component, inject, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { RequestForm } from '../../service/request-form';

interface NavCard {
  title:       string;
  description: string;
  route:       string;
  iconBg:      string;
}

@Component({
  selector: 'app-navigation-cards',
  imports: [RouterLink],
  templateUrl: './navigation-cards.html',
  styleUrl: './navigation-cards.css',
})
export class NavigationCards implements OnInit {
  private request = inject(RequestForm);

  private allNavCards: NavCard[] = [
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
      title: 'Pedidos',
      description: 'Gerencie os pedidos em andamentos no sistema.',
      route: '/pedidos',
      iconBg: 'linear-gradient(135deg, #8b5cf6, #7c3aed)',
    },
  ];

  quickNavCards: NavCard[] = [];

  ngOnInit(): void {
    this.request.executeRequestGET('api/getTelasByPerfil').subscribe({
      next: (telas: { router: string; label: string }[]) => {
        const allowedRoutes = new Set(telas.map(t => t.router));
        this.quickNavCards = this.allNavCards.filter(c => allowedRoutes.has(c.route));
      },
      error: () => {
        // Em caso de erro, não exibe nenhum card (fail-safe)
        this.quickNavCards = [];
      }
    });
  }
}
