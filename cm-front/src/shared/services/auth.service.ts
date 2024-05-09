import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { baseUrl } from '../../assets/constants';


const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}


  private authUrl: string = '/api/auth';
  login(form: any): Observable<any> {
    return this.http.post(
      baseUrl + this.authUrl +'/login',
      form,
      httpOptions
    );
  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(
      baseUrl + this.authUrl +'/signup',
      {
        username,
        email,
        password,
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(baseUrl + this.authUrl+'/signout', { }, httpOptions);
  }

  secondLogin(form: any): Observable<any> {
    return this.http.post(
      baseUrl + this.authUrl +'/secondLogin',
      form,
      httpOptions
    );
  }

  getTenantsByUser(userName: any): Observable<any> {
    return this.http.get(
      baseUrl + this.authUrl +'/getTenantsByUser/'+ userName,
      httpOptions
    );
  }

  loginMaster(form: any): Observable<any> {
    return this.http.post(
      baseUrl + this.authUrl +'/login-master',
      form,
      httpOptions
    );
  }
}
