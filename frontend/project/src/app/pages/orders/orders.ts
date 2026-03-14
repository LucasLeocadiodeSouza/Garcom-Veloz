import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { AlertService } from '../../service/alert-service';

export interface PedidoItemDTO {
  idPedido:      number;
  idItem:        number;
  nomeItem:      string;
  descricaoItem: string;
  quantidade:    number;
  valorItem:     number;
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
  url: string;
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

  isEditModal          = signal<boolean | null>(false);

  confirmDeleteId      = signal<number | null>(null);

  formIdItem     = 0;
  formItem       = '';
  formQuantity   = 0;
  formDescItem   = '';
  formEstoque    = 0;
  formValor      = 0;
  formMesa       = 0;
  formGorgeta    = 0;
  formObservacao = '';

  private themes = ['theme-sunset', 'theme-ocean', 'theme-forest', 'theme-berry', 'theme-dusk', 'theme-mango', 'theme-lavender', 'theme-mint'];

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    this.request.executeRequestGET('api/getListPedidos', { estado: 1 }).subscribe({
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
          id: { idItem: number, seq: number },
          descricao: string,
          criadoEm: Date,
          ideusu: string
        }[] = res;

        const images: ItemMedia[] = mediaResp.map(mp => ({
          idItem: mp.id.idItem,
          url: "http://localhost:8080/api/mediaItem/" + mp.descricao
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

  getSumValueItens() : number {
    var sum = 0;
    this.selectedOrder()?.itens.forEach(item => {
      sum = sum + (item.valorItem * item.quantidade);
    });

    return parseFloat(sum.toFixed(2));
  }

  getTotalValue(){
    return this.getSumValueItens() + this.selectedOrder()!.gorgeta;
  }

  closeCreateOrderModal() {
    this.showCreateOrderModal.set(false);
    this.formIdItem     = 0;
    this.formItem       = '';
    this.formQuantity   = 0;
    this.formDescItem   = '';
    this.formEstoque    = 0;
    this.formValor      = 0;
    this.formMesa       = 0;
    this.formGorgeta    = 0;
    this.formObservacao = '';
  }

  criarAlterarPedido() {
    this.request.executeRequestPOST('api/criarAlterarPedido', { id: this.formItem, mesa: this.formMesa, gorgeta: this.formGorgeta, observacao: this.formObservacao }).subscribe({
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

  closeItensModal() {
    this.formIdItem   = 0;
    this.formItem     = '';
    this.formQuantity = 0;
    this.formDescItem = '';
    this.formEstoque  = 0;
    this.formValor    = 0;
    this.showItensModal.set(null);
    this.isEditModal.set(false);
  }

  incluirItemNoPedido() {
    this.request.executeRequestPOST('api/vinculaItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: this.formIdItem, quantidade: this.formQuantity }).subscribe({
      next: (res: any) => {
        this.closeItensModal();
        this.getListPedidosItem();
        this.loadOrders();
      },
      error: (err) => {
        console.error('Erro ao Criar/Alterar Pedido:', err);
        this.alert.show('Não foi possível criar/alterar o pedido.');
      }
    });
  }

  criarAlterarItemPedido() {
    this.request.executeRequestPOST('api/criarAlterarItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: this.formIdItem, quantidade: this.formQuantity }).subscribe({
      next: (res: any) => {
        this.closeItensModal();
        this.getListPedidosItem();
        this.loadOrders();
      },
      error: (err) => {
        console.error('Erro ao Criar/Alterar Pedido:', err);
        this.alert.show('Não foi possível criar/alterar o pedido.');
      }
    });
  }

  editItem(item: PedidoItemDTO) {
    this.formIdItem   = item.idItem;
    this.formItem     = item.nomeItem;
    this.formDescItem = item.descricaoItem;
    this.formQuantity = item.quantidade;
    this.formValor    = item.valorItem;
    this.showItensModal.set(true);
    this.isEditModal.set(true);
  }

  getListPedidosItem() {
    this.request.executeRequestGET('api/getListPedidosItem', { idPedido: this.selectedOrder()!.id }).subscribe({
      next: (res: PedidoItemDTO[]) => {
        this.selectedOrder()!.itens = res;
      },
      error: (err) => {
        console.error('Erro ao buscar itens do pedido:', err);
        this.alert.show('Não foi possível carregar os itens do pedido.');
      }
    });
  }

  getItemInfo() {
    this.request.executeRequestGET('api/getItemInfo', { idItem: this.formIdItem }).subscribe({
      next: (res: any) => {
        this.formItem     = res == null? "" : res.nome;
        this.formDescItem = res == null? "" : res.descricao;
        this.formQuantity = res == null? 1 : 1;
        this.formEstoque  = res == null? 0 : res.estoque;
        this.formValor    = res == null? 0 : res.valor;
      },
      error: (err) => {
        console.error('Erro ao buscar informações do item:', err);
        this.alert.show('Não foi possível carregar os dados do item.');
      }
    });
  }


  askDelete(dto: PedidoItemDTO) {
    //if(!cat.active) return;
    //if(dto.estado > 0){
    //  this.alert.show('');
    //  return;
    //}

    this.confirmDeleteId.set(dto.idItem);
  }

  cancelDelete() { this.confirmDeleteId.set(null); }

  confirmDelete() {
      const id = this.confirmDeleteId();
      if (id !== null) {

        this.request.executeRequestPOST('api/excluirItemPedido', null, {idPedido: this.selectedOrder()!.id, idItem: id}).subscribe({
          next: () => {
            this.getListPedidosItem();
            this.confirmDeleteId.set(null);
          },
          error: (error) => {
            console.error('Erro:', error);
            this.alert.show('Erro ao excluir categoria. Por favor, tente novamente.');
          }
        });
      }
  }
}
