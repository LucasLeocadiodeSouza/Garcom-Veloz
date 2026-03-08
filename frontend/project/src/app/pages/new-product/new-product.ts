import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Topbar } from '../../layout/topbar/topbar';
import { AlertService } from '../../service/alert-service';
import { RequestForm } from '../../service/request-form';

interface ProductForm {
    name:        string;
    description: string;
    category:    number;
    price:       number | null;
    stock:       number | null;
    unit:        string;
}

@Component({
    selector: 'app-new-product',
    imports: [FormsModule, Topbar],
    templateUrl: './new-product.html',
    styleUrl: './new-product.css'
})
export class NewProduct {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);

  saved     = signal(false);
  formError = signal('');

  form: ProductForm = {
      name:        '',
      description: '',
      category:    0,
      price:       null,
      stock:       null,
      unit:        'un',
  };

  categories: { label: string, value: number }[] = [];
  units = ['un'];

  selectedColor = '#dbeafe';

  activeStep = signal(1); // 1 = Info Básica, 2 = Preço & Estoque, 3 = Confirmação

  constructor(private router: Router) { }

  get steps() {
    return [
        { id: 1, label: 'Informações', icon: '📋' },
        { id: 2, label: 'Preço & Estoque', icon: '💰' },
        { id: 3, label: 'Revisão', icon: '✅' },
    ];
  }

  ngOnInit(){
    this.getAllCategoria();
  }

  goStep(step: number) { this.activeStep.set(step); }

  nextStep() {
      const step = this.activeStep();
      if (step === 1 && (!this.form.name || !this.form.category)) {
          this.formError.set('Preencha o nome e a categoria do produto.');
          return;
      }
      if (step === 2 && (this.form.price === null || this.form.price <= 0)) {
          this.formError.set('Informe um preço válido maior que zero.');
          return;
      }
      this.formError.set('');
      if (step < 3) this.activeStep.set(step + 1);
  }

  prevStep() {
      const step = this.activeStep();
      if (step > 1) this.activeStep.set(step - 1);
  }

  saveProduct() {
      if (!this.form.name || !this.form.category || !this.form.price) {
          this.formError.set('Preencha todos os campos obrigatórios.');
          return;
      }

      this.criarAlterarItem();
  }

  cancel() {
    window.open("/produtos", "_self");
  }

  getSelectedCategory() {
    const seletedValue = this.form.category;

    const selected = this.categories.find(cat => cat.value == seletedValue);

    return selected ? selected.label : null;
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
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar as categoria. Por favor, tente novamente.');
      }
    });
  }

  criarAlterarItem() {
    const dto = {
      nome:        this.form.name,
      descricao:   this.form.description,
      valor:       this.form.price,
      estoque:     this.form.stock,
      desconto:    0,
      idCategoria: this.form.category
    };

    this.request.executeRequestPOST('api/criarAlterarItem', dto).subscribe({
      next: () => {
        this.formError.set('');
        this.saved.set(true);
        setTimeout(() => window.open("/produtos", "_self"), 2000);
      },
      error: (error) => {
        console.error('Erro:', error);
        this.formError.set('Erro ao tentar salvar o Item, confira os dados e tente novamente');
      }
    });
  }
}
