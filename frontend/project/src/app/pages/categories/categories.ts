import { ChangeDetectorRef, Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { map } from 'rxjs/internal/operators/map';
import { AlertService } from '../../service/alert-service';

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
  private alert   = inject(AlertService);

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

      const matchStatus = !this.filterStatus || (this.filterStatus === 'A' ? c.active : !c.active);

      return matchSearch && matchStatus;
    });
  }

  get activeCount() { return this.allCategories.filter(c => c.active).length; }
  get inactiveCount() { return this.allCategories.filter(c => !c.active).length; }
  get userCount() { return this.activeCount + this.inactiveCount; }
  get totalProducts() { return this.allCategories.reduce((s, c) => s + c.productCount, 0); }


  openCreateModal() {
    this.editingCategory.set(null);
    this.formName   = '';
    this.formColor  = '#3b82f6';
    this.formIcon   = '🍽️';
    this.formActive = true;
    this.showModal.set(true);
  }

  openEditModal(cat: Category) {
    if(!cat.active) return;

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

    if(editing){
      const idx = this.allCategories.findIndex(c => c.id === editing.id);
      if(idx == -1) return;
    }

    this.criarAlterarCategoria( (editing?editing.id : 0) , this.formName, this.formIcon, this.formColor);
    this.closeModal();
  }



  toggleActive(cat: Category) {
    const idx = this.allCategories.findIndex(c => c.id === cat.id);
    if (idx == -1) return;

    this.request.executeRequestPOST('api/ativarInativarCategoria', null, {idCategoria: this.allCategories[idx].id, ativar: !this.allCategories[idx].active}).subscribe({
      next: () => {
        this.allCategories[idx].active = !this.allCategories[idx].active;
      },
      error: (error) => {
        console.error('Erro:', error);
      }
    });
  }

  askDelete(cat: Category) {
    if(!cat.active) return;
    if(cat.productCount > 0){
      this.alert.show('Não é possível excluir uma categoria que possui produtos vinculados. Por favor, remova os produtos vinculados antes de excluir a categoria.');
      return;
    }

    this.confirmDeleteId.set(cat.id);
  }

  cancelDelete() { this.confirmDeleteId.set(null); }

  confirmDelete() {
      const id = this.confirmDeleteId();
      if (id !== null) {

        this.request.executeRequestPOST('api/excluirCategoria', null, {idCategoria: id}).subscribe({
          next: () => {
            this.allCategories = this.allCategories.filter(c => c.id !== id);
            this.confirmDeleteId.set(null);
          },
          error: (error) => {
            console.error('Erro:', error);
            this.alert.show('Erro ao excluir categoria. Por favor, tente novamente.');
          }
        });
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
          id:           item.idCategoria,
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
        this.alert.show('Erro ao carregar as categorias. Por favor, tente novamente.');
      }
    });
  }

  criarAlterarCategoria(id: number, descricao: string, icone: string, cor: string) {
    const dto = {
      idCategoria:   id,
      descricao:     descricao,
      icone:         icone,
      cor:           cor
    };

    this.request.executeRequestPOST('api/criarAlterarCategoria', dto).subscribe({
      next: () => {
        this.getCategoriaGrid();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao salvar categoria. Por favor, tente novamente.');
      }
    });
  }
}
