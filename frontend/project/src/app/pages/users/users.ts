import { RequestForm } from './../../service/request-form';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Topbar } from '../../layout/topbar/topbar';
import { AlertService } from '../../service/alert-service';

interface User {
  id:          number;
  name:        string;
  email:       string;
  roleId:      number;
  roleDesc:    string;
  status:      'ativo' | 'inativo';
  phone:       string;
  since:       string;
  initials:    string;
  avatarColor: string;
}

@Component({
  selector: 'app-users',
  imports: [FormsModule, Topbar],
  templateUrl: './users.html',
  styleUrl: './users.css'
})
export class Users implements OnInit {
  private request = inject(RequestForm);
  private alert   = inject(AlertService);

  searchQuery    = '';
  selectedRole   = 0;
  selectedStatus = '';

  allUsers: User[] = [];

  filteredUsers: User[] = [];

  get activeCount() { return this.filteredUsers.filter(u => u.status === 'ativo').length; }
  get inactiveCount() { return this.filteredUsers.filter(u => u.status === 'inativo').length; }
  get userCount() { return this.activeCount + this.inactiveCount; }
  get totAdmins() { return this.filteredUsers.filter(u => u.roleDesc === 'Administrador').length; }

  roles: { label: string, value: number }[] = [];

  permissions: { id: number; name: string; roles: string[] }[] = [];

  showModal = signal(false);
  editingUser = signal<User | null>(null);

  formName     = '';
  formEmail    = '';
  formPhone    = '';
  formRoleId   = 0;

  ngOnInit() { this.getUsuariosGrid(); }

  filterUsers() {
    this.filteredUsers = this.allUsers.filter(u => {
      const matchSearch = !this.searchQuery || u.name.toLowerCase().includes(this.searchQuery.toLowerCase()) || u.email.toLowerCase().includes(this.searchQuery.toLowerCase());

      const matchRole = !this.selectedRole || u.roleId == this.selectedRole;

      const matchStatus = !this.selectedStatus || u.status == this.selectedStatus;

      return matchSearch && matchRole && matchStatus;
    });
  }

  openAddUserModal() {
    this.editingUser.set(null);
    this.formName     = '';
    this.formEmail    = '';
    this.formPhone    = '';
    this.formRoleId   = 0;
    this.showModal.set(true);
  }

  editUser(user: User) {
    this.editingUser.set(user);
    this.formName     = user.name;
    this.formEmail    = user.email;
    this.formPhone    = user.phone;
    this.formRoleId   = user.roleId;
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
    this.editingUser.set(null);
  }

  getUsuariosGrid() {
    this.allUsers = [];

    this.request.executeRequestGET('restrictedApi/getListUsers', { search: this.searchQuery, status: this.selectedStatus, idPerfil: this.selectedRole }).subscribe({
      next: (response: [
        idUsuario:     number,
        nome:          string,
        email:         string,
        perfId:        number,
        perfDescricao: string,
        status:        boolean,
        telefone:      string,
        idEmpresa:     number,
        criadoEm:      Date
      ]) => {

        this.allUsers = response.map((user: any) => ({
          id:          user.idUsuario,
          name:        user.nome,
          email:       user.email,
          roleId:      user.perfId,
          roleDesc:    user.perfDescricao,
          status:      user.status ? 'ativo' : 'inativo',
          phone:       user.telefone,
          since:       user.criadoEm,
          initials:    user.nome.substring(0, 2).toUpperCase(),
          avatarColor: this.getCor(user.idUsuario)

        }));

        this.getAllPerfil();

        this.filterUsers();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar os usuários. Por favor, tente novamente.');
      }
    });
  }

  getAllPerfil() {
    this.request.executeRequestGET('restrictedApi/getAllPerfil').subscribe({
      next: (response: any) => {
        const perfilReq: {
          id: number,
          descricao: string,
          ativo: boolean,
          criadoEm: Date,
        }[] = response;

        this.roles = perfilReq.map(info => ({
          label: info.descricao,
          value: info.id
        }));


        this.getAllRestricoesPerfil();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar os perfis. Por favor, tente novamente.');
      }
    });
  }

  getAllRestricoesPerfil() {
    this.request.executeRequestGET('restrictedApi/getAllRestricoesPerfil').subscribe({
      next: (response: any) => {
        const permissionsReq: {
          perfId: number,
          perfDescricao: string,
          restId: number,
          restDescricao: string
        }[] = response;

        permissionsReq.forEach(info => {
          if (this.permissions.some(p => p.id === info.restId)) return;

          this.permissions.push({
            id: info.restId,
            name: info.restDescricao,
            roles: []
          });
        });

        this.permissions.forEach((req: any) => {
          const matchingRoles = permissionsReq.filter(p => p.restId === req.id).map(p => p.perfDescricao);
          req.roles = matchingRoles;
        });
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao carregar as restricoes. Por favor, tente novamente.');
      }
    });
  }

  criarAlterarUsuario() {
    const dto = {
      idUsuario: this.editingUser()?.id,
      nome:      this.formName,
      email:     this.formEmail,
      telefone:  this.formPhone,
      perfId:    this.formRoleId
    };

    this.request.executeRequestPOST('restrictedApi/criarAlterarUsuario', dto).subscribe({
      next: () => {
        this.getUsuariosGrid();
        this.closeModal();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao salvar usuario. Por favor, tente novamente.');
      }
    });
  }

  ativarInativarUsuario(id: number, ativar: boolean) {
    this.request.executeRequestPOST('restrictedApi/ativarInativarUsuario', null, { idUsuario: id, ativar: ativar }).subscribe({
      next: () => {
        this.getUsuariosGrid();
      },
      error: (error) => {
        console.error('Erro:', error);
        this.alert.show('Erro ao ativar/inativar usuario. Por favor, tente novamente.');
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
      '#fee2e2'
    ]

    return colors[idx % colors.length];
  }
}
