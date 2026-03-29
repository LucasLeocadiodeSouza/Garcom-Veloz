import { Component, inject, ViewChild, ViewContainerRef, HostListener } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { AlertService } from '../../service/alert-service';
import { SlicePipe } from '@angular/common';

@Component({
  selector: 'app-import-export',
  imports: [FormsModule, Topbar, SlicePipe],
  templateUrl: './import-export.html',
  styleUrl: './import-export.css'
})
export class ImportExport {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);
  private http    = inject(HttpClient);

  showInfoTooltip = false;

  @HostListener('document:click')
  onDocumentClick() { this.showInfoTooltip = false; }

  @ViewChild('templateDownloadLink', { read: ViewContainerRef }) templateDownloadLink!: ViewContainerRef;

  exportCategory = '';
  exportStatus   = '';
  isExporting    = false;
  exportSuccess  = false;

  get previewCount() {
    const base = 16;
    return base;
  }

  categories: { label: string, value: string }[] = [];


  selectedFile: File | null = null;
  isDragging                = false;
  isImporting               = false;
  importSuccess             = false;
  importResultMessage       = '';
  overwriteExisting         = false;
  importErrors: string[]    = [];

  showPreviewModal = false;
  previewHeaders: string[] = [];
  previewRows:    any[]    = [];
  columnMapping:  any      = {};

  availableColumns = [
    { value: '',             label: 'Ignorar' },
    { value: 'id',           label: 'ID' },
    { value: 'nome',         label: 'Nome' },
    { value: 'idReferencia', label: 'ID Referência' },
    { value: 'ativo',        label: 'Ativo' },
    { value: 'idCategoria',  label: 'ID Categoria' },
    { value: 'categoria',    label: 'Categoria' },
    { value: 'estoque',      label: 'Estoque' },
    { value: 'vlrItem',      label: 'Vlr Item' },
    { value: 'desconto',     label: 'Desconto' }
  ];


  ngOnInit() {
    this.getAllCategoria();
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
            value: info.id.toString()
        }));
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar as categoria. Por favor, tente novamente.');
      }
    });
  }

  exportExcel() {
    this.isExporting   = true;
    this.exportSuccess = false;

    setTimeout(() => {
      const url = `http://localhost:8080/api/exportarProdutosCSV?status=${this.exportStatus || ''}&idCategoria=${this.exportCategory || ''}`;
      const token = localStorage.getItem('token');

      this.http.get(url, { headers: { Authorization: `Bearer ${token}` }, responseType: 'blob', withCredentials: true }).subscribe({
        next: (blob: Blob) => {
          this.isExporting   = false;
          this.exportSuccess = true;

          const a = this.templateDownloadLink.element.nativeElement;
          const objectUrl = URL.createObjectURL(blob);

          a.href = objectUrl;

          const categName = this.categories.find(c => c.value == this.exportCategory)?.label || '';

          if(this.exportCategory && this.exportStatus) a.download = `produtos_${categName}_${this.exportStatus}.csv`;
          else if(this.exportCategory) a.download = `produtos_${categName}.csv`;
          else if(this.exportStatus) a.download = `produtos_${this.exportStatus}.csv`;
          else a.download = 'produtos.csv';

          a.click();

          URL.revokeObjectURL(objectUrl);

          setTimeout(() => this.exportSuccess = false, 4000);
        },
        error: (err: any) => {
          console.error('Erro ao buscar exportar planilha:', err);
          this.alert.show('Não foi possível exportar a planilha dos produtos.');

          this.isExporting   = false;
          this.exportSuccess = false;
        }
      });
    }, 1800);
  }

  downloadTemplate(e: Event) {
    const url = `http://localhost:8080/api/baixarPlanilhaModelo`;
    const token = localStorage.getItem('token');

    this.http.get(url, { headers: { Authorization: `Bearer ${token}` }, responseType: 'blob', withCredentials: true }).subscribe({
      next: (blob: Blob) => {
          const a = this.templateDownloadLink.element.nativeElement;
          const objectUrl = URL.createObjectURL(blob);

          a.href = objectUrl;
          a.download = 'planilhaModelo.csv';
          a.click();

          URL.revokeObjectURL(objectUrl);
      },
      error: (err: any) => {
        console.error(err);
        this.alert.show('Não foi possível baixar a planilha modelo.');
      }
    });
  }

  onDragOver(e: DragEvent) { e.preventDefault(); this.isDragging = true; }
  onDragLeave() { this.isDragging = false; }
  onDrop(e: DragEvent) {
    e.preventDefault();
    this.isDragging = false;
    const file = e.dataTransfer?.files[0];
    if (file) this.validateAndSetFile(file);
  }

  onFileSelected(e: Event) {
    const file = (e.target as HTMLInputElement).files?.[0];
    if (file) this.validateAndSetFile(file);
  }

  validateAndSetFile(file: File) {
    this.importErrors = [];
    if (!file.name.match(/\.(xlsx|xls|csv)$/i)) {
      this.importErrors = ['Formato inválido. Envie um arquivo .xlsx, .xls ou .csv'];
      return;
    }
    if (file.size > 10 * 1024 * 1024) {
      this.importErrors = ['Arquivo muito grande. Máximo 10MB'];
      return;
    }
    this.selectedFile = file;
    this.importSuccess = false;
  }

  removeFile(e: Event) {
    e.stopPropagation();
    this.selectedFile = null;
    this.importErrors = [];
    this.importSuccess = false;
  }

  formatFileSize(bytes: number): string {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  }

  importExcel() {
    if (!this.selectedFile) return;
    this.isImporting = true;
    this.importErrors = [];

    const formData = new FormData();
    formData.append('file', this.selectedFile);

    this.request.executeRequestPOST('api/previewImportacao', formData).subscribe({
      next: (res: any) => {
        this.isImporting = false;
        this.previewRows = res;
        if(this.previewRows && this.previewRows.length > 0) {
            this.previewHeaders = Object.keys(this.previewRows[0]);
            // Auto mapping
            this.columnMapping = {};
            this.previewHeaders.forEach(h => {
                let hLower = h.toLowerCase();
                let mapped = '';
                if(hLower === 'id' || hLower === 'codigo') mapped = 'id';
                else if(hLower === 'nome') mapped = 'nome';
                else if(hLower === 'ativo') mapped = 'ativo';
                else if(hLower === 'idcategoria' || hLower === 'id categoria') mapped = 'idCategoria';
                else if(hLower === 'categoria') mapped = 'categoria';
                else if(hLower === 'estoque') mapped = 'estoque';
                else if(hLower.includes('vlr') || hLower.includes('valor base')) mapped = 'vlrItem';
                else if(hLower === 'desconto') mapped = 'desconto';
                else if(hLower.includes('liq')) mapped = 'valorLiq';
                this.columnMapping[h] = mapped;
            });
            this.showPreviewModal = true;
        } else {
            this.importErrors = ['Planilha vazia'];
        }
      },
      error: (err: any) => {
        console.error(err);
        this.isImporting = false;
        this.importErrors = [err?.error?.message || 'Erro ao pre-visualizar planilha'];
      }
    });
  }

  cancelImport() {
      this.showPreviewModal = false;
  }

  confirmImport() {
      const itemsToImport = this.previewRows.map(row => {
          let item: any = {};
          this.previewHeaders.forEach(h => {
              const mapped = this.columnMapping[h];
              if(mapped) {
                  let val = row[h];
                  if(mapped === 'id' || mapped === 'idCategoria' || mapped === 'estoque') {
                      item[mapped] = val ? parseInt(val) : null;
                  } else if(mapped === 'vlrItem' || mapped === 'desconto' || mapped === 'valorLiq') {
                      item[mapped] = val ? parseFloat(val.toString().replace(',', '.')) : 0.0;
                  } else if(mapped === 'ativo') {
                      val = val ? val.toString().toLowerCase() : 'false';
                      item[mapped] = (val === 'true' || val === 'sim' || val === '1');
                  } else {
                      item[mapped] = val;
                  }
              }
          });
          return item;
      });

      this.isImporting = true;
      this.showPreviewModal = false;

      this.request.executeRequestPOST('api/importarProdutos', itemsToImport).subscribe({
          next: (res: any) => {
            this.isImporting = false;
            this.importSuccess = true;
            this.importResultMessage = `${itemsToImport.length} produto(s) importado(s) com sucesso!`;

            this.selectedFile = null;
            setTimeout(() => this.importSuccess = false, 4000);
          },
          error: (err: any) => {
              console.error(err);
              this.isImporting = false;
              this.importSuccess = false;
              this.importErrors = [err?.error?.message || 'Erro ao importar produtos'];
          }
      });
  }
}
