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
    window.localStorage.clear();
  }

  public saveUser(user: any): void {
    window.localStorage.removeItem(USER_KEY);
    window.localStorage.setItem(USER_KEY, JSON.stringify(user));
  }

  public saveTenant(tenantName: any): void {
    window.localStorage.removeItem(TENANT_NAME);
    window.localStorage.setItem(TENANT_NAME, tenantName);
  }

  public getUser(): any {
    const user = window.localStorage.getItem(USER_KEY);
   // console.log(user);
    
    if (user) {
      return JSON.parse(user);
    }

    return {};
  }

  public removeUser(): void {
    window.localStorage.removeItem(USER_KEY);
  }

  public getToken(): any {
    return this.getUser().token;
  }

  public getUserName(): any {
    return this.getUser().userName;  
  }

  public getTenantName(): any {
    const tenantName = window.localStorage.getItem(TENANT_NAME);
    return tenantName;
  }

  public isUserLoggedIn(): boolean {
    const user = window.localStorage.getItem(USER_KEY);
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