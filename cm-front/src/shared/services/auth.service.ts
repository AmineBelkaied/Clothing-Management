import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json' })
};

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  constructor(private http: HttpClient) {}

  baseUrl: string = environment.baseUrl;

  private authUrl: string = '/auth';
  login(form: any): Observable<any> {
    
    return this.http.post(
      this.baseUrl + this.authUrl +'/login',
      form,
      httpOptions
    );
  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(
      this.baseUrl + this.authUrl +'/signup',
      {
        username,
        email,
        password,
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(this.baseUrl + this.authUrl + '/signout', { }, httpOptions);
  }

  secondLogin(form: any): Observable<any> {
    return this.http.post(
      this.baseUrl + this.authUrl +'/secondLogin',
      form,
      httpOptions
    );
  }

  getTenantsByUser(userName: any): Observable<any> {
    return this.http.get(
      this.baseUrl + this.authUrl +'/getTenantsByUser/'+ userName,
      httpOptions
    );
  }

  loginMaster(form: any): Observable<any> {
    return this.http.post(
      this.baseUrl + this.authUrl +'/login-master',
      form,
      httpOptions
    );
  }
}
