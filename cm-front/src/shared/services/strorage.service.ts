import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

const USER_KEY = 'auth-user';
const TENANT_NAME = 'tenant-name';


@Injectable({
  providedIn: 'root'
})
export class StorageService {

  isLoggedIn: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  constructor() {
    if(this.isUserLoggedIn())
      this.isLoggedIn.next(true);
  }

  clean(): void {
    window.sessionStorage.clear();
  }

  public saveUser(user: any): void {
    window.sessionStorage.removeItem(USER_KEY);
    window.sessionStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public saveTenant(tenantName: any): void {
    window.sessionStorage.removeItem(TENANT_NAME);
    window.sessionStorage.setItem(TENANT_NAME, tenantName);
  }

  public getUser(): any {
    const user = window.sessionStorage.getItem(USER_KEY);
   // console.log(user);
    
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }

  public removeUser(): void {
    window.sessionStorage.removeItem(USER_KEY);
  }

  public getToken(): any {
    return this.getUser().token;
  }

  public getUserName(): any {
    return this.getUser().userName;  
  }

  public getTenantName(): any {
    const tenantName = window.sessionStorage.getItem(TENANT_NAME);
    return tenantName;
  }

  public isUserLoggedIn(): boolean {
    const user = window.sessionStorage.getItem(USER_KEY);
    if (user) {
      return true;
    }

    return false;
  }

  public getRoles(): any {
    return this.getUser().roles;
  }

  public hasRoleAdmin(): boolean {
    return this.getRoles() && this.getRoles().includes('ROLE_ADMIN');
  }

  public hasRoleSuperAdmin(): boolean {
    return this.getRoles() && this.getRoles().includes('ROLE_SUPERADMIN');
  }
}