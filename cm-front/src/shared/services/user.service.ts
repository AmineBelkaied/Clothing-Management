import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private endPoint: string = "/user";
  
  constructor(private _http: HttpClient) { }

  findAllUsers() {
    return this._http.get(baseUrl + this.endPoint + '/findAll');
  }

  addUser(user: any) {
    return this._http.post(baseUrl + this.endPoint + '/add' , user);
  }

  updateUser(user: any) {
    return this._http.put(baseUrl + this.endPoint + '/update' , user);
  }


  deleteAllUsersById(usersId: string[]) {
    return this._http.delete(baseUrl + this.endPoint + '/deleteAllById/' + usersId);
  }
}
