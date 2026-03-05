import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Products } from './pages/products/products';
import { ImportExport } from './pages/import-export/import-export';
import { Users } from './pages/users/users';
import { Categories } from './pages/categories/categories';
import { NewProduct } from './pages/new-product/new-product';

export const routes: Routes = [

    {
        path: '',
        component: Home,
    },
    {
        path: 'home',
        component: Home
    },
    {
        path: 'produtos',
        component: Products,
        title: 'Produtos - GarçomVeloz CRM'
    },
    {
        path: 'produtos/novo',
        component: NewProduct,
        title: 'Novo Produto - GarçomVeloz CRM'
    },
    {
        path: 'importar-exportar',
        component: ImportExport,
        title: 'Importar / Exportar - GarçomVeloz CRM'
    },
    {
        path: 'usuarios',
        component: Users,
        title: 'Usuários - GarçomVeloz CRM'
    },
    {
        path: 'categorias',
        component: Categories,
        title: 'Categorias - GarçomVeloz CRM'
    }
];
