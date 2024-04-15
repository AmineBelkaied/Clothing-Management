import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class RoleService {

  private endPoint: string = "/role";
  
  constructor(private _http: HttpClient) { }

  findAllRoles() {
    return this._http.get(baseUrl + this.endPoint + '/findAll');
  }

  addRole(role: any) {
    return this._http.post(baseUrl + this.endPoint + '/add' , role);
  }

  updateRole(role: any) {
    return this._http.put(baseUrl + this.endPoint + '/update' , role);
  }

}
