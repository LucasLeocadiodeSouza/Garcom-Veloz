import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { RequestForm } from '../../service/request-form';

@Component({
  selector: 'app-stats-row',
  imports: [],
  templateUrl: './stats-row.html',
  styleUrl: './stats-row.css',
})
export class StatsRow {
  private request = inject(RequestForm);

  stats: {
    cod:       number,
    label:     string,
    value:     string,
    iconBg:    string,
    trendUp:   boolean,
    trendText: string,
    iconColor: string
  }[] = [];

  ngOnInit(): void {
    this.getStats();
  }

  getStats() {
    this.request.executeRequestGET('api/getStatsHome').subscribe({
      next: (
        response: {
          totalprodutos:      number,
          totalUsuariosAtivos:number,
          totalExportacoes:   number,
          totalCategorias:    number,
          produtoMes:         number,
          usuarioMes:         number,
          exportacaoSemana:   number,
          categoriaSemana:    number,
        }) => {

        this.stats = [
          {
            cod: 1,
            label: 'Total de Produtos',
            value: response.totalprodutos.toString(),
            iconBg: '#dbeafe', iconColor: '#2563eb',
            trendUp: response.produtoMes > 0, trendText: response.produtoMes.toString() + ' este mês'
          },
          {
            cod: 2,
            label: 'Usuários Ativos',
            value: response.totalUsuariosAtivos.toString(),
            iconBg: '#d1fae5', iconColor: '#059669',
            trendUp: response.usuarioMes > 0, trendText: response.usuarioMes.toString() + ' este mês'
          },
          {
            cod: 3,
            label: 'Exportações',
            value: response.totalExportacoes.toString(),
            iconBg: '#fef3c7', iconColor: '#d97706',
            trendUp: response.exportacaoSemana > 0, trendText: response.exportacaoSemana.toString() + ' esta semana'
          },
          {
            cod: 4,
            label: 'Categorias',
            value: response.totalCategorias.toString(),
            iconBg: '#ede9fe', iconColor: '#7c3aed',
            trendUp: response.categoriaSemana > 0, trendText: response.categoriaSemana.toString() + ' esta semana'
          }
        ];
      },
      error: (error) => {
        console.error('Erro ao carregar estatísticas:', error);
      }
    });
  }
}
