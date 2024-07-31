import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { ROLE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class RoleService {
  
  private baseUrl: string = environment.baseUrl + `${ROLE_ENDPOINTS.BASE}`;
  
  constructor(private http: HttpClient) { }

  findAllRoles() {
    return this.http.get(`${this.baseUrl}`);
  }

  addRole(role: any) {
    return this.http.post(`${this.baseUrl}`, role , {observe: 'body'});
  }

  updateRole(role: any) {
    return this.http.put(`${this.baseUrl}`, role , {headers : { 'content-type': 'application/json'}});
  }

}
