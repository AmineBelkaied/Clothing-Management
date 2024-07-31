import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { GlobalConf } from '../models/GlobalConf';
import { GLOBAL_CONFIG_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class GlobalConfService {

  private baseUrl: string = environment.baseUrl + `${GLOBAL_CONFIG_ENDPOINTS.BASE}`;
  public editMode = false;

  constructor(private http: HttpClient) {}

  getGlobalConf():Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  updateGlobalConf(globalConf: GlobalConf): Observable<any> {
    return this.http.put(`${this.baseUrl}`, globalConf , {headers : {'content-type': 'application/json'}});
  }
}
