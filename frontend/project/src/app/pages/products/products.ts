import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';

interface Product {
  id: number;
  name: string;
  description: string;
  code: string;
  category: string;
  price: number;
  stock: number;
  status: 'ativo' | 'inativo';
  color: string;
}

@Component({
  selector: 'app-products',
  imports: [FormsModule, Topbar],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products implements OnInit {
  searchQuery = '';
  selectedCategory = '';
  selectedStatus = '';
  currentPage = 1;
  itemsPerPage = 8;
  sortField = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  categories = ['Lanches', 'Bebidas', 'Sobremesas', 'Entradas', 'Pratos Principais', 'Acompanhamentos'];

  allProducts: Product[] = [
    { id: 1, name: 'Hambúrguer Artesanal', description: 'Pão brioche, 180g carne angus', code: 'PROD-001', category: 'Lanches', price: 32.90, stock: 120, status: 'ativo', color: '#dbeafe' },
    { id: 2, name: 'Suco de Laranja', description: 'Natural, 500ml gelado', code: 'PROD-002', category: 'Bebidas', price: 12.00, stock: 85, status: 'ativo', color: '#fef3c7' },
    { id: 3, name: 'Brownie de Chocolate', description: 'Recheio de gotas de chocolate', code: 'PROD-003', category: 'Sobremesas', price: 15.50, stock: 48, status: 'ativo', color: '#ede9fe' },
    { id: 4, name: 'Isca de Frango', description: 'Crocante com molho especial', code: 'PROD-004', category: 'Entradas', price: 22.00, stock: 60, status: 'ativo', color: '#d1fae5' },
    { id: 5, name: 'Prato do Dia', description: 'Frango grelhado com legumes', code: 'PROD-005', category: 'Pratos Principais', price: 38.50, stock: 30, status: 'ativo', color: '#fee2e2' },
    { id: 6, name: 'Batata Frita', description: 'Porção média crocante', code: 'PROD-006', category: 'Acompanhamentos', price: 18.00, stock: 90, status: 'ativo', color: '#fef3c7' },
    { id: 7, name: 'Refrigerante Lata', description: 'Coca, Guaraná, Pepsi', code: 'PROD-007', category: 'Bebidas', price: 7.00, stock: 200, status: 'ativo', color: '#dbeafe' },
    { id: 8, name: 'X-Bacon', description: 'Pão, bacon crocante, queijo', code: 'PROD-008', category: 'Lanches', price: 28.00, stock: 75, status: 'ativo', color: '#dbeafe' },
    { id: 9, name: 'Açaí 500ml', description: 'Com granola e banana', code: 'PROD-009', category: 'Sobremesas', price: 25.00, stock: 40, status: 'ativo', color: '#ede9fe' },
    { id: 10, name: 'Água Mineral', description: 'Garrafa 500ml sem gás', code: 'PROD-010', category: 'Bebidas', price: 5.00, stock: 300, status: 'ativo', color: '#dbeafe' },
    { id: 11, name: 'Salada Caesar', description: 'Alface, croutons, parmesão', code: 'PROD-011', category: 'Entradas', price: 27.00, stock: 8, status: 'ativo', color: '#d1fae5' },
    { id: 12, name: 'Cheesecake', description: 'Com calda de frutas vermelhas', code: 'PROD-012', category: 'Sobremesas', price: 19.90, stock: 22, status: 'inativo', color: '#ede9fe' },
    { id: 13, name: 'Wrap Vegetariano', description: 'Legumes assados e cream cheese', code: 'PROD-013', category: 'Lanches', price: 24.50, stock: 35, status: 'ativo', color: '#dbeafe' },
    { id: 14, name: 'Macarrão Carbonara', description: 'Massa fresca, bacon e ovo', code: 'PROD-014', category: 'Pratos Principais', price: 42.00, stock: 20, status: 'ativo', color: '#fee2e2' },
    { id: 15, name: 'Camarão à Grega', description: 'Camarão, azeite, ervas finas', code: 'PROD-015', category: 'Pratos Principais', price: 68.00, stock: 15, status: 'ativo', color: '#fee2e2' },
    { id: 16, name: 'Sorvete 2 Bolas', description: 'Escolha sabores variados', code: 'PROD-016', category: 'Sobremesas', price: 13.00, stock: 0, status: 'inativo', color: '#ede9fe' },
  ];

  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];

  get activeCount() { return this.filteredProducts.filter(p => p.status === 'ativo').length; }
  get inactiveCount() { return this.filteredProducts.filter(p => p.status === 'inativo').length; }
  get totalPages() { return Math.ceil(this.filteredProducts.length / this.itemsPerPage); }
  get pages() { return Array.from({ length: this.totalPages }, (_, i) => i + 1); }
  get startItem() { return (this.currentPage - 1) * this.itemsPerPage + 1; }
  get endItem() { return Math.min(this.currentPage * this.itemsPerPage, this.filteredProducts.length); }

  ngOnInit() { this.filterProducts(); }

  filterProducts() {
    this.currentPage = 1;
    this.filteredProducts = this.allProducts.filter(p => {
      const matchSearch = !this.searchQuery ||
        p.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        p.code.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchCat = !this.selectedCategory || p.category === this.selectedCategory;
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
    this.selectedCategory = '';
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
}
