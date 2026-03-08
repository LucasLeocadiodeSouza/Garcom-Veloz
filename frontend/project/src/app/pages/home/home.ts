import { Component } from '@angular/core';
import { Topbar } from '../../layout/topbar/topbar';
import { StatsRow } from "../../components/stats-row/stats-row";
import { NavigationCards } from "../../components/navigation-cards/navigation-cards";
import { RecentActivity } from "../../components/recent-activity/recent-activity";

@Component({
  selector: 'app-home',
  imports: [Topbar, StatsRow, NavigationCards, RecentActivity],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
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
