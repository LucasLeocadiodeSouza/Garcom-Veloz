import { ChangeDetectorRef, Component, ElementRef, inject, signal, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { AlertService } from '../../service/alert-service';

interface Product {
  id:          number;
  name:        string;
  description: string;
  code:        string;
  category:    string;
  categoryId:  number;
  price:       number;
  discount:    number;
  stock:       number;
  status:      'A' | 'I';
  color:       string;
}

interface ProductImages {
  id:          number;
  description: string;
}

@Component({
  selector: 'app-products',
  imports: [FormsModule, Topbar],
  templateUrl: './products.html',
  styleUrl: './products.css'
})
export class Products {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);

  constructor(private cdRef: ChangeDetectorRef) {}

  confirmDeleteId = signal<number | null>(null);

  showModal = signal(false);
  editingProduct = signal<Product | null>(null);

  showImagesModal = signal(false);
  productImages = signal<ProductImages[]>([]);
  activeImageIndex = signal(0);

  formId          = 0;
  formName        = '';
  formActive      = true;
  formDescription = '';
  formEstoque     = 0;
  formPrice       = 0;
  formCategoryId  = 0;
  formDiscount    = 0;

  imageProducts: File[] = [];

  searchQuery      = '';
  selectedCategory = 0;
  selectedStatus   = '';
  currentPage      = 1;
  itemsPerPage     = 8;
  sortField        = '';
  sortDirection:   'asc' | 'desc' = 'asc';

  categories: { label: string, value: number }[] = [];

  allProducts: Product[] = [];

  filteredProducts: Product[] = [];
  paginatedProducts: Product[] = [];

  @ViewChild('fileinput') fileInput!: ElementRef<HTMLInputElement>;

  get activeCount() { return this.filteredProducts.filter(p => p.status === 'A').length; }
  get inactiveCount() { return this.filteredProducts.filter(p => p.status === 'I').length; }
  get totalPages() { return Math.ceil(this.filteredProducts.length / this.itemsPerPage); }
  get pages() { return Array.from({ length: this.totalPages }, (_, i) => i + 1); }
  get startItem() { return (this.currentPage - 1) * this.itemsPerPage + 1; }
  get endItem() { return Math.min(this.currentPage * this.itemsPerPage, this.filteredProducts.length); }

  ngOnInit(): void {
    this.getItensGrid();
  }

  filterProducts(keepPage: boolean = false) {
    if (!keepPage) {
      this.currentPage = 1;
    }
    this.filteredProducts = this.allProducts.filter(p => {
      const matchSearch = !this.searchQuery || p.name.toLowerCase().includes(this.searchQuery.toLowerCase()) || p.code == this.searchQuery;

      const matchCat = this.selectedCategory == 0 || p.categoryId == this.selectedCategory;

      const matchStatus = !this.selectedStatus || p.status === this.selectedStatus;

      return matchSearch && matchCat && matchStatus;
    });
    if (this.sortField) this.applySorting();

    if (this.totalPages > 0 && this.currentPage > this.totalPages)this.currentPage = this.totalPages;
    else if (this.totalPages === 0) this.currentPage = 1;

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

  openCreateModal() {
    this.editingProduct.set(null);
    this.formName = '';
    this.formActive = true;
    this.showModal.set(true);
  }
  openEditModal(prod: Product) {
    //if(!prod.status) return;

    this.editingProduct.set(prod);
    this.formId          = prod.id;
    this.formName        = prod.name;
    this.formDescription = prod.description;
    this.formEstoque     = prod.stock;
    this.formPrice       = prod.price;
    this.formActive      = prod.status === 'A';
    this.formCategoryId  = prod.categoryId;
    this.formDiscount    = prod.discount;
    this.showModal.set(true);
  }
  closeModal() {
    this.showModal.set(false);
    this.showImagesModal.set(false);
    this.editingProduct.set(null);
  }


  openImagesModal(prod: Product) {
    this.formId = prod.id;

    this.getListMediaItem();

    this.activeImageIndex.set(0);
    this.showImagesModal.set(true);
  }

  prevImage() {
    const len = this.productImages().length;
    if (len === 0) return;
    this.activeImageIndex.update(i => (i - 1 + len) % len);
  }

  nextImage() {
    const len = this.productImages().length;
    if (len === 0) return;
    this.activeImageIndex.update(i => (i + 1) % len);
  }

  goToImage(index: number) {
    this.activeImageIndex.set(index);
  }

  onImgError(event: Event) {
    const img = event.target as HTMLImageElement;
    img.src = 'assets/mediaError.png';
    img.onerror = null;
  }

  getItensGrid(keepPage: boolean = false) {
    this.allProducts = [];

    this.request.executeRequestGET('api/getItensGrid', { search: this.searchQuery, idCategoria: this.selectedCategory, status: this.selectedStatus }).subscribe({
      next: (response: [
        idItem:        number,
        idCategoria:   number,
        categDecricao: string,
        nome:          string,
        decricao:      string,
        valor:         number,
        desconto:      number,
        estoque:       number,
        ativo:         boolean
      ]) => {

        this.allProducts = response.map((item: any) => ({
          id:          item.idItem,
          name:        item.nome,
          description: item.descricao,
          code:        item.idItem,
          categoryId:  item.idCategoria,
          category:    item.categDecricao,
          price:       item.valor,
          discount:    item.desconto,
          stock:       item.estoque,
          status:      item.ativo ? 'A' : 'I',
          color:       this.getCor(item.idItem)
        }));

        this.getAllCategoria();

        this.filterProducts(keepPage);

        this.cdRef.detectChanges();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar os itens. Por favor, tente novamente.');
      }
    });
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


        this.categories = categoriesReq.map(info => ({
            label: info.descricao,
            value: info.id
        }));

        this.cdRef.detectChanges();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar as categoria. Por favor, tente novamente.');
      }
    });
  }

  askDelete(prod: Product) { this.confirmDeleteId.set(prod.id); }
  cancelDelete() { this.confirmDeleteId.set(null); }
  confirmDelete() {
    const id = this.confirmDeleteId();
    if (id !== null) {
      this.request.executeRequestPOST('api/excluiItem', null, {idItem: id}).subscribe({
        next: () => {
          this.cancelDelete();
          this.getItensGrid(true);
          this.closeModal();
        },
        error: (error) => {
          console.error('Erro:', error);
          this.alert.show('Erro ao excluior o item. Por favor, tente novamente.');
        }
      });
    }
  }

  criarAlterarItem() {
    const dto = {
      idItem:      this.formId,
      nome:        this.formName,
      descricao:   this.formDescription,
      valor:       this.formPrice,
      desconto:    this.formDiscount,
      estoque:     this.formEstoque,
      idCategoria: this.formCategoryId
    };

    this.request.executeRequestPOST('api/criarAlterarItem', dto).subscribe({
      next: () => {
        this.getItensGrid(true);

        this.closeModal();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao salvar o item. Por favor, tente novamente.');
      }
    });
  }

  ativarDesativarItem(idItem: number, ativo: boolean) {
    this.request.executeRequestPOST('api/ativarInativarItem', null, { idItem: idItem, ativar: ativo }).subscribe({
      next: () => {
        this.getItensGrid(true);

        this.cdRef.detectChanges();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao ativar/desativar o item. Por favor, tente novamente.');
      }
    });
  }

  adapterRegisterTempImageItem() {
    this.fileInput.nativeElement.click();
  }

  onFileSelected(event: any) {
    const selectedImage = event.target.files;

    if (selectedImage) {
      this.registerMediaItem(selectedImage);
      event.target.value = '';
    }
  }

  registerMediaItem(files: File[]){
    const formData = new FormData();

    for (let file of files) {
      formData.append("images[]", file);
    }
    formData.append("idItem", this.formId.toString());

    this.request.executeRequestPOST('api/registrarMediaProduct', formData).subscribe({
      next: (response) => {
        this.getItensGrid(true);
        this.closeModal();
       },
      error: (error) => {
        alert(error.error);
        console.error('Erro:', error);
      }
    });
  }

  getListMediaItem() {
    this.request.executeRequestGET('api/getListMediaItem', {idItem: this.formId}).subscribe({
      next: (response: any) => {
        const mediaResp: {
	        id:		        { idItem: number, seq: number },
          descricao:    string,
          criadoEm:     Date,
          ideusu:       string
        }[] = response;

        const images: ProductImages[] = mediaResp.map(mp => ({
          id: mp.id.seq,
          description: "http://localhost:8080/api/mediaItem/" + mp.descricao
        }));

        this.productImages.set(images);

        this.cdRef.detectChanges();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar as mídias. Por favor, tente novamente.');
      }
    });
  }



  getCor(idx: number) {
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
