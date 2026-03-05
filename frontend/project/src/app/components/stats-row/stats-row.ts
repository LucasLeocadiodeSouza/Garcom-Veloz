import { ChangeDetectorRef, Component, inject } from '@angular/core';
import { RequestForm } from '../../service/request-form';

@Component({
  selector: 'app-stats-row',
  imports: [],
  templateUrl: './stats-row.html',
  styleUrl: './stats-row.css',
})
export class StatsRow {
  // private request = inject(RequestForm);

  // constructor(private cdRef: ChangeDetectorRef) { }

  // ngOnInit(): void {
  //   this.getStats();
  // }

  // getStats() {
  //   this.request.executeRequestGET('api/getStats', null).subscribe({
  //     next: (
  //       response: {
  //         totalProducts: number,
  //         activeUsers: number,
  //         totalExports: number,
  //         totalCategories: number
  //       }) => {


  //       this.stats.forEach(stat => {
  //         if (stat.cod == 1) {
  //           stat.value = response.totalProducts.toString();
  //         }
  //         if (stat.cod == 2) {
  //           stat.value = response.activeUsers.toString();
  //         }
  //         if (stat.cod == 3) {
  //           stat.value = response.totalExports.toString();
  //         }
  //         if (stat.cod == 4) {
  //           stat.value = response.totalCategories.toString();
  //         }
  //       });

  //       this.cdRef.detectChanges();
  //     },
  //     error: (error) => {
  //       console.error('Erro ao carregar estatísticas:', error);
  //     }
  //   });
  // }

  stats = [
    {
      cod: 1,
      label: 'Total de Produtos',
      value: '248',
      iconBg: '#dbeafe', iconColor: '#2563eb',
      trendUp: true, trendText: '+12% este mês'
    },
    {
      cod: 2,
      label: 'Usuários Ativos',
      value: '14',
      iconBg: '#d1fae5', iconColor: '#059669',
      trendUp: true, trendText: '+2 novos'
    },
    {
      cod: 3,
      label: 'Exportações',
      value: '37',
      iconBg: '#fef3c7', iconColor: '#d97706',
      trendUp: true, trendText: '+5 esta semana'
    },
    {
      cod: 4,
      label: 'Categorias',
      value: '18',
      iconBg: '#ede9fe', iconColor: '#7c3aed',
      trendUp: false, trendText: '-1 inativa'
    },
  ];
}
