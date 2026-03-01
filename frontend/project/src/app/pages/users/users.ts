import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';

interface User {
  id: number;
  name: string;
  email: string;
  role: string;
  status: 'ativo' | 'inativo';
  lastAccess: string;
  since: string;
  initials: string;
  avatarColor: string;
}

@Component({
  selector: 'app-users',
  imports: [FormsModule, Topbar],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {
  searchQuery = '';
  selectedRole = '';
  selectedStatus = '';

  allUsers: User[] = [
    { id: 1, name: 'Lucas Leocadio', email: 'lucas@garcomveloz.com.br', role: 'Administrador', status: 'ativo', lastAccess: 'hoje, 20:45', since: 'Jan/2024', initials: 'LL', avatarColor: '#1e40af' },
    { id: 2, name: 'Ana Paula Souza', email: 'ana.paula@garcomveloz.com.br', role: 'Gerente', status: 'ativo', lastAccess: 'hoje, 18:30', since: 'Mar/2024', initials: 'AP', avatarColor: '#059669' },
    { id: 3, name: 'Carlos Mendes', email: 'carlos.m@garcomveloz.com.br', role: 'Atendente', status: 'ativo', lastAccess: 'ontem, 22:10', since: 'Jun/2024', initials: 'CM', avatarColor: '#7c3aed' },
    { id: 4, name: 'Fernanda Costa', email: 'fernanda@garcomveloz.com.br', role: 'Atendente', status: 'ativo', lastAccess: 'hoje, 19:00', since: 'Jun/2024', initials: 'FC', avatarColor: '#d97706' },
    { id: 5, name: 'João Silva', email: 'joao.silva@garcomveloz.com.br', role: 'Cozinha', status: 'ativo', lastAccess: 'ontem, 14:20', since: 'Set/2024', initials: 'JS', avatarColor: '#06b6d4' },
    { id: 6, name: 'Maria Oliveira', email: 'maria.o@garcomveloz.com.br', role: 'Cozinha', status: 'inativo', lastAccess: '10/01/2026', since: 'Ago/2024', initials: 'MO', avatarColor: '#94a3b8' },
    { id: 7, name: 'Rafael Torres', email: 'rafael.t@garcomveloz.com.br', role: 'Gerente', status: 'ativo', lastAccess: 'hoje, 17:55', since: 'Nov/2024', initials: 'RT', avatarColor: '#dc2626' },
  ];

  filteredUsers: User[] = [];

  userStats = [
    {
      label: 'Total', value: '7',
      icon: `<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M17 21v-2a4 4 0 00-4-4H5a4 4 0 00-4 4v2" stroke="currentColor" stroke-width="2" stroke-linecap="round"/><circle cx="9" cy="7" r="4" stroke="currentColor" stroke-width="2"/><path d="M23 21v-2a4 4 0 00-3-3.87M16 3.13a4 4 0 010 7.75" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`,
      iconBg: '#dbeafe', iconColor: '#1d4ed8'
    },
    {
      label: 'Ativos', value: '6',
      icon: `<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" stroke="currentColor" stroke-width="2"/></svg>`,
      iconBg: '#d1fae5', iconColor: '#059669'
    },
    {
      label: 'Inativos', value: '1',
      icon: `<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M18.364 18.364A9 9 0 005.636 5.636m12.728 12.728A9 9 0 015.636 5.636m12.728 12.728L5.636 5.636" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`,
      iconBg: '#fee2e2', iconColor: '#dc2626'
    },
    {
      label: 'Administradores', value: '1',
      icon: `<svg viewBox="0 0 24 24" fill="none" width="20" height="20"><path d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" stroke="currentColor" stroke-width="2" stroke-linecap="round"/></svg>`,
      iconBg: '#ede9fe', iconColor: '#7c3aed'
    },
  ];

  roles = ['Administrador', 'Gerente', 'Atendente', 'Cozinha'];
  permissions = [
    { name: 'Visualizar Produtos', roles: ['Administrador', 'Gerente', 'Atendente', 'Cozinha'] },
    { name: 'Editar Produtos', roles: ['Administrador', 'Gerente'] },
    { name: 'Excluir Produtos', roles: ['Administrador'] },
    { name: 'Importar/Exportar Excel', roles: ['Administrador', 'Gerente'] },
    { name: 'Gerenciar Usuários', roles: ['Administrador'] },
    { name: 'Ver Relatórios', roles: ['Administrador', 'Gerente'] },
    { name: 'Registrar Pedidos', roles: ['Administrador', 'Gerente', 'Atendente'] },
    { name: 'Visualizar Cardápio', roles: ['Administrador', 'Gerente', 'Atendente', 'Cozinha'] },
  ];

  ngOnInit() { this.filterUsers(); }

  filterUsers() {
    this.filteredUsers = this.allUsers.filter(u => {
      const matchSearch = !this.searchQuery ||
        u.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        u.email.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchRole = !this.selectedRole || u.role === this.selectedRole;
      const matchStatus = !this.selectedStatus || u.status === this.selectedStatus;
      return matchSearch && matchRole && matchStatus;
    });
  }

  openAddUserModal() { alert('Funcionalidade de adicionar usuário (a implementar)'); }
  editUser(user: User) { alert(`Editar usuário: ${user.name} (a implementar)`); }

  toggleUserStatus(user: User) {
    user.status = user.status === 'ativo' ? 'inativo' : 'ativo';
    this.filterUsers();
  }

  deleteUser(id: number) {
    if (confirm('Tem certeza que deseja excluir este usuário?')) {
      this.allUsers = this.allUsers.filter(u => u.id !== id);
      this.filterUsers();
    }
  }
}
