import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { map } from 'rxjs/internal/operators/map';

interface Category {
    id: number;
    name: string;
    color: string;
    icon: string;
    productCount: number;
    active: boolean;
    createdAt: string;
}

@Component({
    selector: 'app-categories',
    imports: [FormsModule, Topbar],
    templateUrl: './categories.html',
    styleUrl: './categories.css'
})
export class Categories {
    private request = inject(RequestForm);

  ngOnInit(): void {
    this.getCategoriaGrid();
  }

    searchQuery = '';
    filterStatus = '';
    showModal = signal(false);
    editingCategory = signal<Category | null>(null);
    confirmDeleteId = signal<number | null>(null);

    formName = '';
    formColor = '#3b82f6';
    formIcon = '🍽️';
    formActive = true;

    iconOptions = ['🍽️', '🥤', '🍔', '🍕', '🥗', '🍰', '🍷', '🥩', '🌮', '🍜', '🍣', '🧃'];
    colorOptions = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#14b8a6', '#f97316'];

    allCategories: Category[] = [];

    get filtered() {
        return this.allCategories.filter(c => {
            const matchSearch = !this.searchQuery || c.name.toLowerCase().includes(this.searchQuery.toLowerCase());

            const matchStatus = !this.filterStatus || (this.filterStatus === 'ativo' ? c.active : !c.active);

            return matchSearch && matchStatus;
        });
    }

    get activeCount() { return this.allCategories.filter(c => c.active).length; }
    get totalProducts() { return this.allCategories.reduce((s, c) => s + c.productCount, 0); }

    openCreateModal() {
        this.editingCategory.set(null);
        this.formName = '';
        this.formColor = '#3b82f6';
        this.formIcon = '🍽️';
        this.formActive = true;
        this.showModal.set(true);
    }

    openEditModal(cat: Category) {
        this.editingCategory.set(cat);
        this.formName = cat.name;
        this.formColor = cat.color;
        this.formIcon = cat.icon;
        this.formActive = cat.active;
        this.showModal.set(true);
    }

    closeModal() {
        this.showModal.set(false);
        this.editingCategory.set(null);
    }

    saveCategory() {
        const editing = this.editingCategory();
        if (editing) {
            const idx = this.allCategories.findIndex(c => c.id === editing.id);
            if (idx !== -1) {
                this.allCategories[idx] = {
                    ...this.allCategories[idx],
                    name: this.formName,
                    color: this.formColor,
                    icon: this.formIcon,
                    active: this.formActive,
                };
            }
        } else {
            const newCat: Category = {
                id: Date.now(),
                name: this.formName,
                color: this.formColor,
                icon: this.formIcon,
                productCount: 0,
                active: this.formActive,
                createdAt: new Date().toISOString().split('T')[0],
            };
            this.allCategories.push(newCat);
        }
        this.closeModal();
    }

    toggleActive(cat: Category) {
        const idx = this.allCategories.findIndex(c => c.id === cat.id);
        if (idx !== -1) this.allCategories[idx].active = !this.allCategories[idx].active;
    }

    askDelete(id: number) { this.confirmDeleteId.set(id); }
    cancelDelete() { this.confirmDeleteId.set(null); }
    confirmDelete() {
        const id = this.confirmDeleteId();
        if (id !== null) {
            this.allCategories = this.allCategories.filter(c => c.id !== id);
            this.confirmDeleteId.set(null);
        }
    }

  getCategoriaGrid(){
    this.request.executeRequestGET('api/getCategoriaGrid', {search: this.searchQuery, status: this.filterStatus}).subscribe({
      next: (response: [
        idCategoria:   number,
        descricao:     string,
        icone:         string,
        cor:           string,
        status:        boolean,
        totalProdVinc: number
      ]) => {

        this.allCategories = response.map((item: any) => ({
          id:           item.id,
          name:         item.descricao,
          color:        item.cor,
          icon:         item.icone,
          productCount: item.totalProdVinc,
          active:       item.status,
          createdAt:    item.criadoEm.split('T')[0]
        }));
      },
      error: (error) => {
        console.error('Erro:', error);
      }
    });
  }
}
