import { Injectable } from '@angular/core';
import { HttpClient, HttpRequest, HttpHeaders, HttpEvent } from '@angular/common/http';
import { Observable } from 'rxjs';
import { StorageService } from './strorage.service';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class TenantService {

  private endPoint: string = baseUrl + "/tenant";

/*   private httpOptions = {
    headers: new HttpHeaders({'Content-Type': 'application/json', 'Accept': 'application/json', 'Authorization' : 'Bearer ' + this.storageService.getToken()})
  }
   */
  private httpOptions = {
    headers: new HttpHeaders({'Authorization' : 'Bearer ' + this.storageService.getToken()})
  }
  constructor(private http: HttpClient, private storageService: StorageService) { }

  upload(file: File): Observable<HttpEvent<any>> {
    //console.log("cwxcwc");
    //console.log(file);
    
    const formData: any = new FormData();

    formData.append('file', file);
    console.log(...formData);
    const req = new HttpRequest('POST',this.endPoint + "/upload", formData, this.httpOptions);
    console.log(req);
    
    return this.http.request(req);
  }

  addTenant(tenantName: string) {   
    return this.http.get(this.endPoint + "/add/" + tenantName);
  }

  findAllTenants() {
    return this.http.get(this.endPoint + "/findAll/", this.httpOptions);
  }
/* 
  getFiles(): Observable<any> {
    return this.http.get(`${this.baseUrl}/files`);
  } */
}