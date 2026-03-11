import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { AlertService } from '../../service/alert-service';

export interface PedidoItemDTO {
  idPedido:   number;
  idItem:     number;
  nomeItem:   string;
  quantidade: number;
  valorItem:  number;
}

export interface PedidoDTO {
  id:          number;
  estado:      number;
  observacao:  string;
  gorgeta:     number;
  mesa:        number;
  criadoEm:    string;
  horario:     string;
  ideusu:      string;
  valorTotal:  number;
  itens:       PedidoItemDTO[];
  colorTheme?: string;
}

export interface ItemMedia {
  idItem: number;
  url:    string;
}

@Component({
  selector: 'app-orders',
  imports: [FormsModule, Topbar],
  templateUrl: './orders.html',
  styleUrl: './orders.css'
})
export class Orders implements OnInit {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);

  orders: PedidoDTO[] = [];

  selectedOrder        = signal<PedidoDTO | null>(null);
  selectedItemForMedia = signal<PedidoItemDTO | null>(null);
  itemMediaList        = signal<any[]>([]);

  showCreateOrderModal = signal<boolean | null>(null);
  showItensModal       = signal<boolean | null>(null);
  formsOrderModal      = signal<PedidoDTO | null>(null);

  formIdItem   = 0;
  formItem     = '';
  formQuantity = 0;
  formDescItem = '';
  formEstoque  = 0;
  formValor    = 0;
  formMesa     = 0
  formGorgeta  = 0;

  private themes = [ 'theme-sunset', 'theme-ocean', 'theme-forest', 'theme-berry', 'theme-dusk', 'theme-mango', 'theme-lavender', 'theme-mint' ];

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.request.executeRequestGET('api/getListPedidos').subscribe({
      next: (res: any) => {
        this.orders = res.map((order: PedidoDTO) => {
          order.colorTheme = this.getRandomTheme();
          return order;
        });
      },
      error: (err) => {
        console.error('Erro ao buscar pedidos:', err);
        this.alert.show('Não foi possível carregar os pedidos.');
      }
    });
  }

  getRandomTheme(): string {
    const randomIndex = Math.floor(Math.random() * this.themes.length);
    return this.themes[randomIndex];
  }

  openOrderDetails(order: PedidoDTO) {
    this.selectedOrder.set(order);
  }

  closeOrderDetails() {
    this.selectedOrder.set(null);
    this.closeMediaModal();
  }

  openMediaModal(item: PedidoItemDTO) {
    this.selectedItemForMedia.set(item);

    this.request.executeRequestGET('api/getListMediaItem', { idItem: item.idItem }).subscribe({
      next: (res: any) => {
        const mediaResp: {
	        id:		        { idItem: number, seq: number },
          descricao:    string,
          criadoEm:     Date,
          ideusu:       string
        }[] = res;

        const images: ItemMedia[] = mediaResp.map(mp => ({
          idItem: mp.id.idItem,
          url:    "http://localhost:8080/api/mediaItem/" + mp.descricao
        }));

        this.itemMediaList.set(images);
      },
      error: (err) => {
        console.error('Erro ao buscar mídia:', err);
        this.itemMediaList.set([]);
      }
    });
  }

  closeMediaModal() {
    this.selectedItemForMedia.set(null);
    this.itemMediaList.set([]);
  }

  formatTime(timeArray: number[] | string): string {
    if (Array.isArray(timeArray)) {
      const hours = timeArray[0].toString().padStart(2, '0');
      const minutes = timeArray[1].toString().padStart(2, '0');
      return `${hours}:${minutes}`;
    }
    return timeArray as string;
  }

  closeCreateOrderModal(){
    this.showCreateOrderModal.set(false);
    this.formIdItem   = 0;
    this.formItem     = '';
    this.formQuantity = 0;
    this.formDescItem = '';
    this.formEstoque  = 0;
    this.formValor    = 0;
    this.formMesa     = 0;
    this.formGorgeta  = 0;
  }

  criarAlterarPedido(){
    this.request.executeRequestPOST('api/criarAlterarPedido', {id: this.formItem, mesa: this.formMesa, gorgeta: this.formGorgeta }).subscribe({
      next: (res: any) => {
          this.loadOrders();

          this.closeCreateOrderModal();
      },
      error: (err) => {
        console.error('Erro ao Criar/Alterar Pedido:', err);
        this.alert.show('Não foi possível criar/alterar o pedido.');
      }
    });
  }

  closeItensModal(){
    this.showItensModal.set(null);
    this.formIdItem   = 0;
    this.formItem     = '';
    this.formQuantity = 0;
    this.formDescItem = '';
    this.formEstoque  = 0;
    this.formValor    = 0;
  }

  incluirItemNoPedido(){
    this.request.executeRequestPOST('api/criarAlterarItemPedido', null, {IdPedido: this.selectedOrder()!.id, IdItem: this.formIdItem }).subscribe({
      next: (res: any) => {
          this.getListPedidosItem();
      },
      error: (err) => {
        console.error('Erro ao Criar/Alterar Pedido:', err);
        this.alert.show('Não foi possível criar/alterar o pedido.');
      }
    });
  }

  getListPedidosItem(){
    this.request.executeRequestGET('api/getListPedidosItem', {IdPedido: this.selectedOrder()!.id }).subscribe({
      next: (res: PedidoItemDTO[]) => {
          this.selectedOrder()!.itens = res;
      },
      error: (err) => {
        console.error('Erro ao buscar itens do pedido:', err);
        this.alert.show('Não foi possível carregar os itens do pedido.');
      }
    });
  }

  getItemInfo(){
    this.request.executeRequestGET('api/getItemInfo', {idItem: this.formIdItem }).subscribe({
      next: (res: any) => {
          this.formItem     = res.nome;
          this.formDescItem = res.descricao;
          this.formQuantity = 0;
          this.formEstoque  = res.estoque;
          this.formValor    = res.valor;
      },
      error: (err) => {
        console.error('Erro ao buscar dados do item:', err);
        this.alert.show('Não foi possível carregar os dados do item.');
      }
    });
  }
}
