import { Component, OnInit, signal, inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser, CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { Location } from '@angular/common';
import { RequestForm } from '../../service/request-form';
import { read } from 'node:fs';

interface Product {
  id:          number;
  name:        string;
  description: string;
  category:    string;
  categoryId:  number;
  price:       number;
  discount:    number;
  stock:       number;
  mediaPath:   string;
}

@Component({
  selector: 'app-cardapio',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './cardapio.html',
  styleUrl: './cardapio.css'
})
export class Cardapio implements OnInit {
  private route      = inject(ActivatedRoute);
  private request    = inject(RequestForm);
  private router     = inject(Router);
  private location   = inject(Location);
  private platformId = inject(PLATFORM_ID);

  activeMesa = signal<string | null>(null);

  categories: { label: string, value: number | null, icone: string }[] = [];

  allProducts: Product[] = [];

  searchQuery = '';
  selectedCategory: number | null = 0;

  carrinho: { product: Product, quantity: number }[] = [];
  isCartOpen = false;
  showSuccessModal = signal<boolean>(false);

  ngOnInit() {
    if (isPlatformBrowser(this.platformId)) {
      this.route.queryParams.subscribe(params => {
        const mesaParam = params['mesa'];

        if (mesaParam) {
          sessionStorage.setItem('sessao_mesa', mesaParam);
          this.activeMesa.set(mesaParam);

          this.location.replaceState('/cardapio');
        } else { // Se não tiver o parâmetro, tenta carregar da sessão (caso a pessoa tenha recarregado a página ou etc...)
          const storedMesa = sessionStorage.getItem('sessao_mesa');
          if (storedMesa) this.activeMesa.set(storedMesa);
        }
      });
      this.getItensForCardapio();
    }
  }

  sairDaMesa() {
    if (isPlatformBrowser(this.platformId)) {
      sessionStorage.removeItem('sessao_mesa');
      this.activeMesa.set(null);
    }
  }

  filterByCategory(categoryId: number | null) {
    this.selectedCategory = categoryId;
    this.getItensForCardapio();
  }

  onSearch(event: Event) {
    const input = event.target as HTMLInputElement;
    this.searchQuery = input.value;
    this.getItensForCardapio();
  }

  getAllCategoria() {
    this.request.executeRequestGET('api/getAllCategoria').subscribe({
      next: (response: any) => {
        const categoriesReq: {
          id:           number,
          descricao:    string,
          icone:        string,
          cor:          string,
          refereciaExt: number,
          ativo:        boolean,
          criadoEm:     Date,
          ideusu:       string
        }[] = response;


        const mappedCategories = categoriesReq.map(info => ({
            value: info.id,
            label: info.descricao,
            icone: info.icone
        }));

        this.categories = [
          { label: 'Todos', value: 0, icone: '🍽️' },
          ...mappedCategories
        ];

      },
      error: (error) => {
        console.error('Erro:', error);
      }
    });
  }

  getItensForCardapio(){
    this.allProducts = [];

    this.request.executeRequestGET('api/getItensForCardapio', { search: this.searchQuery, idCategoria: this.selectedCategory, status: "A" }).subscribe({
      next: (response: [
        idItem:        number,
        idCategoria:   number,
        categDecricao: string,
        nome:          string,
        decricao:      string,
        valor:         number,
        desconto:      number,
        estoque:       number,
        mediaPath:     string
      ]) => {

        this.allProducts = response.map((item: any) => ({
          id:          item.idItem,
          name:        item.nome,
          description: item.descricao,
          categoryId:  item.idCategoria,
          category:    item.categDecricao,
          price:       item.valor,
          discount:    item.desconto,
          stock:       item.estoque,
          mediaPath:   item.mediaPath ? "http://localhost:8080/api/mediaItem/" + item.mediaPath : "",
        }));

        this.getAllCategoria();
      },
      error: (error) => {
        console.error('Erro:', error);
      }
    });
  }



  toggleCart() {
    if (this.carrinho.length > 0) {
      this.isCartOpen = !this.isCartOpen;
    }
  }

  closeSuccessModal() {
    this.showSuccessModal.set(false);
  }

  adicionarItemAoCarrinho(prod: Product) {
    const item = this.carrinho.find(item => item.product.id === prod.id);
    if (item) item.quantity++;
    else this.carrinho.push({ product: prod, quantity: 1 });
  }

  removerItemDoCarrinho(prod: Product) {
    const index = this.carrinho.findIndex(item => item.product.id === prod.id);
    if (index !== -1) {
      this.carrinho[index].quantity--;
      if (this.carrinho[index].quantity === 0) this.carrinho.splice(index, 1);
      if (this.carrinho.length === 0) this.isCartOpen = false;
    }
  }

  getQuantidadeNoCarrinho(prodId: number): number {
    const item = this.carrinho.find(item => item.product.id === prodId);
    return item ? item.quantity : 0;
  }

  getTotalCarrinho(): number {
    return this.carrinho.reduce((acc, item) => {
      const precoFinal = item.product.price - (item.product.discount || 0);
      return acc + (precoFinal * item.quantity);
    }, 0);
  }

  get totalItemsCart(): number {
    return this.carrinho.reduce((acc, item) => acc + item.quantity, 0);
  }


  criarPedido() {
    const itens = this.carrinho.map(item => ({
                    idItem:     item.product.id,
                    quantidade: item.quantity
                  }))

    const pedidoDto = {
      observacao: "",
      mesa:       this.activeMesa(),
      ideusu:     "EXTERNO",
      itens:      itens
    }

    this.request.executeRequestPOST('api/adapterCriarPedido', pedidoDto).subscribe({
      next: (response: any) => {
        this.carrinho   = [];
        this.isCartOpen = false;
        this.showSuccessModal.set(true);
      },
      error: (error) => {
        console.error('Erro:', error);
      }
    });
  }
}
