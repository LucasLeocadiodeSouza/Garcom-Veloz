import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';

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
  imports: [FormsModule, Topbar],
  templateUrl: './import-export.html',
  styleUrl: './import-export.css'
})
export class ImportExport {
  // Export
  exportCategory = '';
  exportStatus = '';
  isExporting = false;
  exportSuccess = false;

  exportFields = [
    { key: 'code', label: 'Código', selected: true },
    { key: 'name', label: 'Nome', selected: true },
    { key: 'category', label: 'Categoria', selected: true },
    { key: 'price', label: 'Preço', selected: true },
    { key: 'stock', label: 'Estoque', selected: true },
    { key: 'status', label: 'Status', selected: true },
    { key: 'desc', label: 'Descrição', selected: false },
  ];

  get previewCount() {
    const base = 16;
    return base;
  }

  categories = ['Lanches', 'Bebidas', 'Sobremesas', 'Entradas', 'Pratos Principais', 'Acompanhamentos'];

  // Import
  selectedFile: File | null = null;
  isDragging = false;
  isImporting = false;
  importSuccess = false;
  importResultMessage = '';
  importErrors: string[] = [];
  overwriteExisting = false;
  skipErrors = true;

  // History
  history: HistoryItem[] = [
    { id: 1, datetime: '27/02/2026 14:32', type: 'Exportação', file: 'produtos_export_27022026.xlsx', records: 248, status: 'Concluído' },
    { id: 2, datetime: '25/02/2026 09:15', type: 'Importação', file: 'novos_produtos_fev.xlsx', records: 12, status: 'Concluído' },
    { id: 3, datetime: '20/02/2026 16:48', type: 'Exportação', file: 'produtos_ativos.xlsx', records: 236, status: 'Concluído' },
    { id: 4, datetime: '18/02/2026 11:05', type: 'Importação', file: 'produtos_jan_update.xlsx', records: 0, status: 'Erro' },
  ];
  private nextHistoryId = 5;

  exportExcel() {
    this.isExporting = true;
    this.exportSuccess = false;
    setTimeout(() => {
      this.isExporting = false;
      this.exportSuccess = true;
      const filename = `produtos_export_${new Date().toLocaleDateString('pt-BR').replace(/\//g, '')}.xlsx`;
      this.history.unshift({
        id: this.nextHistoryId++,
        datetime: new Date().toLocaleString('pt-BR').replace(',', ''),
        type: 'Exportação',
        file: filename,
        records: this.previewCount,
        status: 'Concluído'
      });
      setTimeout(() => this.exportSuccess = false, 4000);
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
    if (!file.name.match(/\.(xlsx|xls)$/i)) {
      this.importErrors = ['Formato inválido. Envie um arquivo .xlsx ou .xls'];
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
    this.importSuccess = false;
    setTimeout(() => {
      this.isImporting = false;
      this.importSuccess = true;
      const count = Math.floor(Math.random() * 20) + 5;
      this.importResultMessage = `${count} produto(s) importado(s) com sucesso!`;
      this.history.unshift({
        id: this.nextHistoryId++,
        datetime: new Date().toLocaleString('pt-BR').replace(',', ''),
        type: 'Importação',
        file: this.selectedFile!.name,
        records: count,
        status: 'Concluído'
      });
      this.selectedFile = null;
      setTimeout(() => this.importSuccess = false, 4000);
    }, 2000);
  }

  clearHistory() { this.history = []; }
}
