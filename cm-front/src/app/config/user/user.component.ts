import { Component, ViewChild } from '@angular/core';
import { Table } from 'jspdf-autotable';
import { ConfirmationService, MessageService, SelectItem } from 'primeng/api';
import { concatMap } from 'rxjs';
import { User } from 'src/shared/models/User';
import { RoleService } from 'src/shared/services/role.service';
import { TenantService } from 'src/shared/services/tenant.service';
import { UserService } from 'src/shared/services/user.service';

@Component({
  selector: 'app-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.scss']
})
export class UserComponent {

  users: any[] = [];

  userDialog!: boolean;

  user!: User;

  oldUser: any;

  submitted!: boolean;

  editMode = false;

  selectedUsers: SelectItem[] = [];

  tenants: any[] = [];

  roles: any[] = [];
  
  userTenantsName: any[] = [];

  selectedTenants: any[] = [];
  
  newPassword!: string;

  @ViewChild("dt")
  table!: Table;


  constructor(private userService: UserService, private tenantService: TenantService,
    private roleService: RoleService, private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit() {
    this.userService.findAllUsers()
    .pipe(concatMap((users: any) => {
      this.users = users;
/*       this.userTenantsName = this.users.map(user => user.masterTenants).map(tenant => tenant.tenantName); */
/*       this.userTenantsName = this.users.map((user:any) => user.masterTenants.map((tenant: any) =>  tenant = tenant.tenantName));
      console.log(this.userTenantsName); */
      
      return this.roleService.findAllRoles();
    }))
    .subscribe((roles: any) => {
        this.roles = roles;
        console.log(roles);
      });
  }

  getAllUsers() {
    this.userService.findAllUsers()
    .subscribe((users: any) => {
        this.users = users;
        console.log(users);
      });
  }

  openNew() {
    this.user = {};
    this.newPassword = "";
    this.selectedTenants = [];
    this.submitted = false;
    this.userDialog = true;
    this.editMode = false;
  }

  editUser(user: any) {

    this.user = { ...user };
        console.log( this.user );
    //this.selectedTenants = this.user.masterTenants.map((masterTenant: any) => masterTenant.tenantClientId);
    console.log(this.selectedTenants);
    
    this.submitted = false;
    this.userDialog = true;
    this.editMode = true;
  }

  saveUser() {
    if(this.newPassword != null && this.newPassword != "")
     this.user.password = this.newPassword;
    
    if(!this.invalidUser(this.user)) {
      if (this.editMode) {
        this.userService.updateUser(this.user)
        .subscribe((result: any) => {
          console.log(result)
          this.getAllUsers();
          this.hideDialog();
          this.messageService.add({ severity: 'info', summary: 'Succés', detail: "L'utilisateur a été modifié avec succés" , life: 1000 });
        });
      } else {
        this.userService.addUser(this.user)
          .subscribe((result: any) => {
            console.log(result)
            this.getAllUsers();
            this.hideDialog();
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "L'utilisateur a été ajouté avec succés" , life: 1000 });
          });
      }
    }
  }

  deleteSelectedUsers() {
    let ids = this.selectedUsers.map((selectedUsers: any) => selectedUsers.userId)
    this.confirmationService.confirm({
      message: 'Êtes vous sure de vouloir supprimer le(s) utilisateurs sélectionnées ?',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.userService.deleteAllUsersById(ids)
          .subscribe((result: any) => {
            this.getAllUsers();
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les utilisateurs sélectionnés ont été supprimé avec succés' , life: 1000 });
            this.selectedUsers = [];
          });
      }
    });
  }

  hideDialog() {
    this.userDialog = false;
  }
  
  displayTenants(tenants: any) {
    return tenants.map((tenant: any) => tenant.tenantName)
  }

  invalidUser(user: User): boolean {
    return (!user.userName || (!this.editMode && !this.newPassword) || !user.roles || user.roles.length === 0);
  }

  getRoles(roles: any[]): string[] {
    return roles.map(role => role.description)
  }
}
