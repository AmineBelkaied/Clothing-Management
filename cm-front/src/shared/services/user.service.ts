import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private baseUrl: string = environment.baseUrl + "/user";
  
  constructor(private _http: HttpClient) { }

  findAllUsers() {
    return this._http.get(this.baseUrl + '/findAll');
  }

  addUser(user: any) {
    return this._http.post(this.baseUrl + '/add' , user);
  }

  updateUser(user: any) {
    return this._http.put(this.baseUrl + '/update' , user);
  }


  deleteAllUsersById(usersId: string[]) {
    return this._http.delete(this.baseUrl + '/deleteAllById/' + usersId);
  }
}
