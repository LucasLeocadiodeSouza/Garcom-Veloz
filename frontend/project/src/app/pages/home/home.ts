import { Component, inject } from '@angular/core';
import { Topbar } from '../../layout/topbar/topbar';
import { StatsRow } from "../../components/stats-row/stats-row";
import { NavigationCards } from "../../components/navigation-cards/navigation-cards";
import { RecentActivity } from "../../components/recent-activity/recent-activity";
import { RequestForm } from '../../service/request-form';
import { UserService } from '../../service/user-service';

@Component({
  selector: 'app-home',
  imports: [Topbar, StatsRow, NavigationCards, RecentActivity],
  templateUrl: './home.html',
  styleUrl: './home.css'
})
export class Home {
  private request     = inject(RequestForm);
  private userService = inject(UserService);

  ngOnInit(){ this.getInfoUser() }

  getInfoUser(): void {
    this.request.executeRequestGET('api/getInfoUser').subscribe({
      next: (response: {username: string, perfil: string}) => {
        this.userService.setUser({
          username: response.username,
          perfil:   response.perfil
        });
      },
      error: (error) => console.error("Erro ao carregar informacoes do username: ", error)
    });
  }


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
