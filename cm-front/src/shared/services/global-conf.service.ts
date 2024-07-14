import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { GlobalConf } from '../models/GlobalConf';

@Injectable({
  providedIn: 'root'
})
export class GlobalConfService {


  private baseUrl: string = environment.baseUrl + "/globalConf";
  public editMode = false;

  constructor(private http: HttpClient) {}


  getGlobalConf():Observable<any> {
    return this.http.get<any>(this.baseUrl + "/get");
  }

  updateGlobalConf(globalConf: GlobalConf): Observable<any> {
    return this.http.put<any>(this.baseUrl + "/update", globalConf);
  }

}
