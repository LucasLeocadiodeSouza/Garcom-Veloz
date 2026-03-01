import { Routes } from '@angular/router';
import { Home } from './pages/home/home';
import { Products } from './pages/products/products';
import { ImportExport } from './pages/import-export/import-export';
import { Users } from './pages/users/users';

export const routes: Routes = [
    { path: '', redirectTo: 'home', pathMatch: 'full' },
    { path: 'home', component: Home, title: 'Dashboard – GarçomVeloz CRM' },
    { path: 'produtos', component: Products, title: 'Produtos – GarçomVeloz CRM' },
    { path: 'importar-exportar', component: ImportExport, title: 'Importar / Exportar – GarçomVeloz CRM' },
    { path: 'usuarios', component: Users, title: 'Usuários – GarçomVeloz CRM' },
    { path: '**', redirectTo: 'home' },
];
