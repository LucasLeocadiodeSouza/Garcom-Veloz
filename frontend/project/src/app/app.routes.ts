import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Products } from './pages/products/products';
import { ImportExport } from './pages/import-export/import-export';
import { Users } from './pages/users/users';
import { Categories } from './pages/categories/categories';
import { NewProduct } from './pages/new-product/new-product';
import { Orders } from './pages/orders/orders';
import { Login } from './pages/login/login';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [

    {
        path: '',
        redirectTo: '/login',
        pathMatch: 'full'
    },
    {
        path: 'login',
        component: Login,
        title: 'Login - GarçomVeloz CRM'
    },
    {
        path: 'home',
        component: Home
    },
    {
        path: 'produtos',
        component: Products,
        title: 'Produtos - GarçomVeloz CRM',
        canActivate: [authGuard]
    },
    {
        path: 'pedidos',
        component: Orders,
        title: 'Pedidos - GarçomVeloz CRM',
        canActivate: [authGuard]
    },
    {
        path: 'produtos/novo',
        component: NewProduct,
        title: 'Novo Produto - GarçomVeloz CRM',
        canActivate: [authGuard]
    },
    {
        path: 'importar-exportar',
        component: ImportExport,
        title: 'Importar / Exportar - GarçomVeloz CRM',
        canActivate: [authGuard]
    },
    {
        path: 'usuarios',
        component: Users,
        title: 'Usuários - GarçomVeloz CRM',
        canActivate: [authGuard]
    },
    {
        path: 'categorias',
        component: Categories,
        title: 'Categorias - GarçomVeloz CRM',
        canActivate: [authGuard]
    }
];
