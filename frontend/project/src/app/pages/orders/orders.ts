import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { AlertService } from '../../service/alert-service';

export interface PedidoItemDTO {
  idPedido:      number;
  idItem:        number;
  seq:           number;
  nomeItem:      string;
  descricaoItem: string;
  quantidade:    number;
  valorItem:     number;
  descontoItem:  number;
  estado:        number;
  descEstado:    string;
}

export interface PedidoDTO {
  id:          number;
  estado:      number;
  descEstado:  string;
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

export interface ZoomProduct {
  idItem:    number;
  nome:      string;
  descricao: string;
  valor:     number;
  desconto:  number;
  estoque:   number;
  categoria: string;
}

@Component({
  selector: 'app-orders',
  imports: [FormsModule, Topbar, DecimalPipe],
  templateUrl: './orders.html',
  styleUrl: './orders.css'
})
export class Orders implements OnInit {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);

  orders: PedidoDTO[] = [];

  filterSearch   = signal<string>('');
  filterStatus   = signal<string>('all');
  filterDateFrom = signal<string>(this.getDefaultDateFrom());
  filterDateTo   = signal<string>(this.getDefaultDateTo());

  private getDefaultDateTo(): string { return new Date().toISOString().slice(0, 10) }

  private getDefaultDateFrom(): string { return new Date().toLocaleDateString('en-CA').slice(0, 10) }



  get filteredOrders(): PedidoDTO[] {
    const search = this.filterSearch().toLowerCase().trim();
    const status = this.filterStatus();

    return this.orders.filter(o => {
      const matchSearch = !search || o.id.toString().includes(search) || o.mesa.toString().includes(search);
      const matchStatus = status === 'all' || o.estado.toString() === status;
      return matchSearch && matchStatus;
    });
  }

  selectedOrder        = signal<PedidoDTO | null>(null);
  selectedItemForMedia = signal<PedidoItemDTO | null>(null);
  itemMediaList        = signal<any[]>([]);

  showCreateOrderModal = signal<boolean | null>(null);
  showItensModal       = signal<boolean | null>(null);
  formsOrderModal      = signal<PedidoDTO | null>(null);

  showObservacao = signal<boolean | null>(null);

  isEditModal = signal<boolean | null>(false);

  showModalConfirm   = signal<boolean | null>(null);
  actionModalConfirm = signal<number | null>(null);
  titleModalConfirm  = signal<string | null>(null);
  textModalConfirm   = signal<string | null>(null);

  confirmDeleteId    = signal<{ idItem: number, seq: number } | null>(null);

  showZoomModal    = signal<boolean>(false);
  zoomAllProducts: ZoomProduct[] = [];
  zoomSearch       = '';

  get zoomFilteredProducts(): ZoomProduct[] {
    const q = this.zoomSearch.toLowerCase().trim();
    if (!q) return this.zoomAllProducts;
    return this.zoomAllProducts.filter(p =>
      p.nome.toLowerCase().includes(q) || p.idItem.toString().includes(q) || (p.categoria || '').toLowerCase().includes(q)
    );
  }

  openZoomModal() {
    this.zoomSearch = '';
    if (this.zoomAllProducts.length === 0) {
      this.request.executeRequestGET('api/getItensGrid', {}).subscribe({
        next: (res: any) => {
          this.zoomAllProducts = res.filter((item: any) => item.ativo).map((item: any) => ({
              idItem:    item.idItem,
              nome:      item.nome,
              descricao: item.descricao,
              valor:     item.valor,
              desconto:  item.desconto,
              estoque:   item.estoque,
              categoria: item.categDecricao
            }));
        },
        error: () => this.alert.show('Não foi possível carregar os produtos.')
      });
    }
    this.showZoomModal.set(true);
  }

  closeZoomModal() { this.showZoomModal.set(false); }

  selectZoomProduct(prod: ZoomProduct) {
    this.formIdItem = prod.idItem;
    this.getItemInfo();
    this.closeZoomModal();
  }

  formIdItem     = 0;
  formSeq        = 0;
  formItem       = '';
  formQuantity   = 0;
  formDescItem   = '';
  formEstoque    = 0;
  formValor      = 0;
  formDiscount   = 0;
  formMesa       = 0;
  formGorgeta    = 0;
  formObservacao = '';

  private themes = ['theme-sunset', 'theme-ocean', 'theme-forest', 'theme-berry', 'theme-dusk', 'theme-mango', 'theme-lavender', 'theme-mint'];

  ngOnInit() {
    this.loadOrders();
  }

  loadOrders() {
    const params: any = {
      dataInicio: this.filterDateFrom(),
      dataFim: this.filterDateTo()
    };
    this.request.executeRequestGET('api/getListPedidos', params).subscribe({
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

  openOrderDetails(order: PedidoDTO) { this.selectedOrder.set(order) }

  closeOrderDetails() {
    this.selectedOrder.set(null);
    this.showObservacao.set(false);
    this.closeMediaModal();
  }

  cancelModalConfirm() { this.showModalConfirm.set(null); }

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

  getSumValueItens(): number {
    var sum = 0;
    this.selectedOrder()?.itens.forEach(item => {
      const valueProd = item.descontoItem > 0 ? item.valorItem - item.descontoItem : item.valorItem;

      sum = sum + (valueProd * item.quantidade);
    });

    return parseFloat(sum.toFixed(2));
  }

  getTotalValue(){
    return this.getSumValueItens() + this.selectedOrder()!.gorgeta;
  }

  isEstadoAbertoPedido(){ return this.selectedOrder()?.estado === 1 }
  isEstadoConfirmadoPedido(){ return this.selectedOrder()?.estado === 2 }

  closeCreateOrderModal() {
    this.showCreateOrderModal.set(false);
    this.formIdItem     = 0;
    this.formSeq        = 0;
    this.formItem       = '';
    this.formQuantity   = 0;
    this.formDescItem   = '';
    this.formEstoque    = 0;
    this.formValor      = 0;
    this.formDiscount   = 0;
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

  askAlterarEstadoPedido(estado: number){
    this.showModalConfirm.set(true);

    if(estado === 1){
      this.titleModalConfirm.set('Retorno');
      this.textModalConfirm.set('Deseja retomar o pedido? O pedido retornará para o estado em aberto.');
      this.actionModalConfirm.set(2);
    }
    else if(estado === 2){
      this.titleModalConfirm.set('Pedido');
      this.textModalConfirm.set('Deseja encerrar o pedido? Essa ação não permitirá mais alterações.');
      this.actionModalConfirm.set(3);
    }else{
      this.titleModalConfirm.set('Cancelamento');
      this.textModalConfirm.set('Deseja cancelar o pedido? Essa ação não permitirá mais alterações.');
      this.actionModalConfirm.set(4);
    }
  }

  cancelarPedido(){
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Cancelamento de pedido somente é possiveis quando está em estado ABERTO.');
      return;
    }

    this.request.executeRequestPOST('api/alterarEstadoPedido', null, { idPedido: this.selectedOrder()!.id, estado: 0 }).subscribe({
      next: () => {
        this.loadOrders();
        this.cancelModalConfirm();
        this.closeOrderDetails();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao cancelar o Pedido. Por favor, tente novamente.');
      }
    });
  }
  encerrarPedido(){
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Encerramento de pedido somente é possivel quando está em estado ABERTO.');
      return;
    }

    this.request.executeRequestPOST('api/alterarEstadoPedido', null, { idPedido: this.selectedOrder()!.id, estado: 2 }).subscribe({
      next: () => {
        this.loadOrders();
        this.cancelModalConfirm();
        this.closeOrderDetails();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao encerrar o Pedido. Por favor, tente novamente.');
      }
    });
  }



  closeItensModal() {
    this.formIdItem   = 0;
    this.formSeq      = 0;
    this.formItem     = '';
    this.formQuantity = 0;
    this.formDescItem = '';
    this.formEstoque  = 0;
    this.formValor    = 0;
    this.formDiscount = 0;
    this.showItensModal.set(null);
    this.isEditModal.set(false);
  }

  incluirItemNoPedido() {
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Somente é possivel incluir um novo item no pedido quando o pedido está em estado ABERTO.');
      return;
    }

    this.request.executeRequestPOST('api/vinculaItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: this.formIdItem, seq: this.formSeq, quantidade: this.formQuantity }).subscribe({
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

  getItemInfo() {
    this.request.executeRequestGET('api/getItemInfo', { idItem: this.formIdItem }).subscribe({
      next: (res: any) => {
        this.formItem     = res == null ? "" : res.nome;
        this.formSeq      = 0; // Sempre inicia com 0 para nova inclusão
        this.formDescItem = res == null ? "" : res.descricao;
        this.formQuantity = res == null ? 1 : 1;
        this.formEstoque  = res == null ? 0 : res.estoque;
        this.formValor    = res == null ? 0 : res.valor;
        this.formDiscount = res == null ? 0 : res.desconto;
      },
      error: (err) => {
        console.error('Erro ao buscar informações do item:', err);
        this.alert.show('Não foi possível carregar os dados do item.');
      }
    });
  }

  criarAlterarItemPedido() {
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Somente é possivel alterar um item no pedido quando o pedido está em estado ABERTO.');
      return;
    }

    this.request.executeRequestPOST('api/criarAlterarItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: this.formIdItem, seq: this.formSeq, quantidade: this.formQuantity }).subscribe({
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

  isEstadoAbertoItemPedido(dto: PedidoItemDTO){ return dto.estado === 1 }
  isEstadoConfirmadoItemPedido(dto: PedidoItemDTO){ return dto.estado === 2 }

  editItem(item: PedidoItemDTO) {
    this.formIdItem   = item.idItem;
    this.formSeq      = item.seq;
    this.formItem     = item.nomeItem;
    this.formDescItem = item.descricaoItem;
    this.formQuantity = item.quantidade;
    this.formValor    = item.valorItem;
    this.formDiscount = item.descontoItem;
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

  temItemConfirmadoOuEntregue(): boolean {
    return this.selectedOrder()?.itens.some(item => item.estado === 2 || item.estado === 3) ?? false;
  }


  askDelete(dto: PedidoItemDTO) {
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Somente é possivel excluir um item no pedido quando o pedido está em estado ABERTO.');
      return;
    }

    if(dto.estado === 3){
      this.alert.show('Não é possivel deletar um item que já está entregue. Por favor, tente novamente.');
      return;
    }

    this.showModalConfirm.set(true);
    this.titleModalConfirm.set('Exclusão');
    this.textModalConfirm.set('Tem certeza que deseja excluir este item do pedido? Está ação não pode ser desfeita.');
    this.actionModalConfirm.set(1);
    this.confirmDeleteId.set({ idItem: dto.idItem, seq: dto.seq });
  }

  confirmDelete() {
    this.cancelModalConfirm();

    const id = this.confirmDeleteId();
    if (id !== null) {
      this.request.executeRequestPOST('api/excluirItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: id.idItem, seq: id.seq }).subscribe({
        next: () => {
          this.getListPedidosItem();
          this.loadOrders();
          this.confirmDeleteId.set(null);
        },
        error: (error) => {
          console.error('Erro:', error);
          this.alert.show('Erro ao excluir item do Pedido. Por favor, tente novamente.');
        }
      });
    }
  }

  confirmarItemPedido(dto: PedidoItemDTO) {
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Somente é possivel confirmar itens quando o pedido está em estado ABERTO.');
      return;
    }

    this.request.executeRequestPOST('api/alterarEstadoItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: dto.idItem, seq: dto.seq, estado: 2 }).subscribe({
      next: () => {
        this.getListPedidosItem();
        this.loadOrders();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao confirmar o item. Por favor, tente novamente.');
      }
    });
  }
  encerrarItemPedido(dto: PedidoItemDTO) {
    if(this.selectedOrder()!.estado !== 1){
      this.alert.show('Somente é possivel encerrar itens quando o pedido está em estado ABERTO.');
      return;
    }

    this.request.executeRequestPOST('api/alterarEstadoItemPedido', null, { idPedido: this.selectedOrder()!.id, idItem: dto.idItem, seq: dto.seq, estado: 3 }).subscribe({
      next: () => {
        this.getListPedidosItem();
        this.loadOrders();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao confirmar o item. Por favor, tente novamente.');
      }
    });
  }
}
