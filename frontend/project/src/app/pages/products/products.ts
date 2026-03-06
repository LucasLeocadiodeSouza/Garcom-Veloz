import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';

interface Product {
  id: number;
  name: string;
  description: string;
  code: string;
  category: string;
  categoryId: number;
  price: number;
  stock: number;
  status: 'A' | 'I';
  color: string;
}

@Component({
  selector: 'app-products',
  imports: [FormsModule, RouterLink, Topbar],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products {
  private request = inject(RequestForm);

  ngOnInit(): void {
    this.getItensGrid();
    this.filterProducts();
  }


  searchQuery = '';
  selectedCategory = 0;
  selectedStatus = '';
  currentPage = 1;
  itemsPerPage = 8;
  sortField = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  categories = [{ label: '', value: 0 }];

  allProducts: Product[] = [ ];

  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];

  get activeCount() { return this.filteredProducts.filter(p => p.status === 'A').length; }
  get inactiveCount() { return this.filteredProducts.filter(p => p.status === 'I').length; }
  get totalPages() { return Math.ceil(this.filteredProducts.length / this.itemsPerPage); }
  get pages() { return Array.from({ length: this.totalPages }, (_, i) => i + 1); }
  get startItem() { return (this.currentPage - 1) * this.itemsPerPage + 1; }
  get endItem() { return Math.min(this.currentPage * this.itemsPerPage, this.filteredProducts.length); }

  filterProducts() {
    this.currentPage = 1;
    this.filteredProducts = this.allProducts.filter(p => {
      const matchSearch = !this.searchQuery || p.name.toLowerCase().includes(this.searchQuery.toLowerCase()) || p.code == this.searchQuery;

      const matchCat = this.selectedCategory == 0 || p.categoryId == this.selectedCategory;

      const matchStatus = !this.selectedStatus || p.status === this.selectedStatus;

      return matchSearch && matchCat && matchStatus;
    });
    if (this.sortField) this.applySorting();
    this.paginate();
  }

  sort(field: string) {
    if (this.sortField === field) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortField = field;
      this.sortDirection = 'asc';
    }
    this.applySorting();
    this.paginate();
  }

  applySorting() {
    this.filteredProducts.sort((a, b) => {
      const aVal = (a as any)[this.sortField];
      const bVal = (b as any)[this.sortField];
      const cmp = typeof aVal === 'string' ? aVal.localeCompare(bVal) : aVal - bVal;
      return this.sortDirection === 'asc' ? cmp : -cmp;
    });
  }

  getSortIcon(field: string) {
    if (this.sortField !== field) return '↕';
    return this.sortDirection === 'asc' ? '↑' : '↓';
  }

  clearFilters() {
    this.searchQuery = '';
    this.selectedCategory = 0;
    this.selectedStatus = '';
    this.sortField = '';
    this.filterProducts();
  }

  paginate() {
    const start = (this.currentPage - 1) * this.itemsPerPage;
    this.paginatedProducts = this.filteredProducts.slice(start, start + this.itemsPerPage);
  }

  goToPage(page: number) { this.currentPage = page; this.paginate(); }
  prevPage() { if (this.currentPage > 1) { this.currentPage--; this.paginate(); } }
  nextPage() { if (this.currentPage < this.totalPages) { this.currentPage++; this.paginate(); } }


  getItensGrid(){
    this.request.executeRequestGET('api/getItensGrid', {search: this.searchQuery, idCategoria: this.selectedCategory, status: this.selectedStatus}).subscribe({
      next: (response: [
        idItem:         number,
        idCategoria:    number,
        categDecricao:  string,
        nome:           string,
        decricao:       string,
        valor:          number,
        desconto:       number,
        estoque:        number,
        ativo:          boolean
      ]) => {

        this.allProducts = response.map((item: any) => ({
          id:          item.idItem,
          name:        item.nome,
          description: item.decricao,
          code:        item.idItem,
          categoryId:  item.idCategoria,
          category:    item.categDecricao,
          price:       item.valor,
          stock:       item.estoque,
          status:      item.ativo ? 'A' : 'I',
          color:       this.getCor(item.idItem)
        }));


        this.categories = [];
        this.allProducts.filter(p => {
          if(!this.categories.includes({ label: p.category, value: p.categoryId }))  this.categories.push({ label: p.category, value: p.categoryId });
        });
      },
      error: (error) => {
        console.error('Erro:', error);
        //this.showAlertMessage.set('Erro ao carregar as categorias. Por favor, tente novamente.');
      }
    });
  }


  getCor(idx: number){
    const colors = [
    '#dbeafe',
    '#fef3c7',
    '#ede9fe',
    '#d1fae5',
    '#fee2e2',
    '#fef3c7',
    '#dbeafe',
    '#dbeafe',
    '#ede9fe',
    '#dbeafe',
    '#d1fae5',
    '#ede9fe',
    '#dbeafe',
    '#fee2e2',
    '#fee2e2']


    return colors[idx % colors.length];
  }
}
