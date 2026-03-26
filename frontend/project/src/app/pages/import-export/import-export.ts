import { Component, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { RequestForm } from '../../service/request-form';
import { AlertService } from '../../service/alert-service';
import { SlicePipe } from '@angular/common';

interface HistoryItem {
  id: number;
  datetime: string;
  type: 'Exportação' | 'Importação';
  file: string;
  records: number;
  status: 'Concluído' | 'Erro';
}

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


  exportCategory = '';
  exportStatus   = '';
  isExporting    = false;
  exportSuccess  = false;

  get previewCount() {
    const base = 16;
    return base;
  }

  categories = ['Lanches', 'Bebidas', 'Sobremesas', 'Entradas', 'Pratos Principais', 'Acompanhamentos'];


  selectedFile: File | null = null;
  isDragging          = false;
  isImporting         = false;
  importSuccess       = false;
  importResultMessage = '';
  overwriteExisting   = false;
  importErrors: string[] = [];

  showPreviewModal = false;
  previewHeaders: string[] = [];
  previewRows: any[] = [];
  columnMapping: any = {};

  availableColumns = [
    { value: '',            label: 'Ignorar' },
    { value: 'id',          label: 'ID' },
    { value: 'nome',        label: 'Nome' },
    { value: 'ativo',       label: 'Ativo' },
    { value: 'idCategoria', label: 'ID Categoria' },
    { value: 'categoria',   label: 'Categoria' },
    { value: 'estoque',     label: 'Estoque' },
    { value: 'vlrItem',     label: 'Vlr Item' },
    { value: 'desconto',    label: 'Desconto' },
    { value: 'valorLiq',    label: 'Valor Liq' }
  ];

  exportExcel() {
    this.isExporting   = true;
    this.exportSuccess = false;

    setTimeout(() => {
      const url = `http://localhost:8080/api/exportarProdutosCSV?status=${this.exportStatus || ''}`;
      const token = localStorage.getItem('token');
      this.http.get(url, { headers: { Authorization: `Bearer ${token}` }, responseType: 'blob', withCredentials: true }).subscribe({
        next: (blob: Blob) => {
          this.isExporting   = false;
          this.exportSuccess = true;

          const a = document.createElement('a');
          const objectUrl = URL.createObjectURL(blob);
          a.href = objectUrl;
          a.download = 'produtos.csv';
          document.body.appendChild(a);
          a.click();
          URL.revokeObjectURL(objectUrl);
          document.body.removeChild(a);

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
    e.preventDefault();
    alert('Modelo de planilha baixado! (simulação)');
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
