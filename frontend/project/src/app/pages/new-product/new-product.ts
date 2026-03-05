import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Topbar } from '../../layout/topbar/topbar';

interface ProductForm {
    name: string;
    description: string;
    category: string;
    price: number | null;
    stock: number | null;
    status: 'ativo' | 'inativo';
    unit: string;
}

@Component({
    selector: 'app-new-product',
    imports: [FormsModule, Topbar],
    templateUrl: './new-product.html',
    styleUrl: './new-product.css'
})
export class NewProduct {
    saved = signal(false);
    formError = signal('');

    form: ProductForm = {
        name: '',
        description: '',
        category: '',
        price: null,
        stock: null,
        status: 'ativo',
        unit: 'un',
    };

    categories = ['Lanches', 'Bebidas', 'Sobremesas', 'Entradas', 'Pratos Principais', 'Acompanhamentos'];
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
        this.formError.set('');
        this.saved.set(true);
        setTimeout(() => this.router.navigate(['/produtos']), 2000);
    }

    cancel() {
        this.router.navigate(['/produtos']);
    }
}
