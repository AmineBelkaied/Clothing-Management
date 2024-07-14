import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  
  private baseUrl: string = environment.baseUrl + "/role";
  
  constructor(private _http: HttpClient) { }

  findAllRoles() {
    return this._http.get(this.baseUrl  + '/findAll');
  }

  addRole(role: any) {
    return this._http.post(this.baseUrl + '/add' , role);
  }

  updateRole(role: any) {
    return this._http.put(this.baseUrl + '/update' , role);
  }

}
