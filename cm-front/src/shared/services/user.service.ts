import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { USER_ENDPOINTS } from '../constants/api-endpoints';
import { User } from '../models/User';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl: string = environment.baseUrl + `${USER_ENDPOINTS.BASE}`;

  constructor(private http: HttpClient) { }

  findAllUsers() {
    return this.http.get(`${this.baseUrl}`);
  }

  addUser(user: User) {
    return this.http.post(`${this.baseUrl}`, user , {observe: 'body'});
  }

  updateUser(user: User) {
    return this.http.put(`${this.baseUrl}`, user , {headers : { 'content-type': 'application/json'}});
  }

  deleteAllUsersById(usersId: number[]) {
    return this.http.delete(`${this.baseUrl}/${USER_ENDPOINTS.BATCH_DELETE}/${usersId}`);
  }
}
