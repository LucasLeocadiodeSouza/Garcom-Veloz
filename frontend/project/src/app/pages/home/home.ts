import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Topbar } from '../../layout/topbar/topbar';

@Component({
  selector: 'app-home',
  imports: [RouterLink, Topbar],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  stats = [
    {
      label: 'Total de Produtos',
      value: '248',
      iconBg: '#dbeafe', iconColor: '#2563eb',
      trendUp: true, trendText: '+12% este mês'
    },
    {
      label: 'Usuários Ativos',
      value: '14',
      iconBg: '#d1fae5', iconColor: '#059669',
      trendUp: true, trendText: '+2 novos'
    },
    {
      label: 'Exportações',
      value: '37',
      iconBg: '#fef3c7', iconColor: '#d97706',
      trendUp: true, trendText: '+5 esta semana'
    },
    {
      label: 'Categorias',
      value: '18',
      iconBg: '#ede9fe', iconColor: '#7c3aed',
      trendUp: false, trendText: '-1 inativa'
    },
  ];

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

  recentActivity = [
    { text: 'Produto "Hambúrguer Artesanal" cadastrado', time: 'há 5 min', color: '#3b82f6' },
    { text: 'Planilha exportada com 248 produtos', time: 'há 22 min', color: '#10b981' },
    { text: 'Usuário João Silva adicionado', time: 'há 1h', color: '#8b5cf6' },
    { text: 'Produto "Suco de Laranja" atualizado', time: 'há 2h', color: '#f59e0b' },
    { text: 'Importação de 12 novos produtos', time: 'ontem', color: '#06b6d4' },
  ];

  quickActions = [
    {
      label: 'Novo Produto',
      route: '/produtos'
    },
    {
      label: 'Exportar Excel',
      route: '/importar-exportar'
    },
    {
      label: 'Adicionar Usuário',
      route: '/usuarios'
    },
    {
      label: 'Importar Planilha',
      route: '/importar-exportar'
    },
  ];
}
