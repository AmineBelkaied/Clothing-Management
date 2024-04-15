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

  
  private baseUrl: string = baseUrl + '/auth';
  login(form: any): Observable<any> {
    return this.http.post(
      baseUrl + '/api/auth/login',
      form,
      httpOptions
    );
  }

  register(username: string, email: string, password: string): Observable<any> {
    return this.http.post(
      baseUrl + 'api/auth/signup',
      {
        username,
        email,
        password,
      },
      httpOptions
    );
  }

  logout(): Observable<any> {
    return this.http.post(baseUrl + 'api/auth/signout', { }, httpOptions);
  }

  secondLogin(form: any): Observable<any> {
    return this.http.post(
      baseUrl + 'api/auth/secondLogin',
      form,
      httpOptions
    );
  }

  getTenantsByUser(userName: any): Observable<any> {
    return this.http.get(
      baseUrl + 'api/auth/getTenantsByUser/'+ userName,
      httpOptions
    );
  }

  loginMaster(form: any): Observable<any> {
    return this.http.post(
      baseUrl + '/api/auth/login-master',
      form,
      httpOptions
    );
  }
}