import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, shareReplay } from 'rxjs';
import { baseUrl } from 'src/assets/constants';
import { GlobalConf } from '../models/GlobalConf';

@Injectable({
  providedIn: 'root'
})
export class GlobalConfService {


  private baseUrl: string = baseUrl + "/globalConf";
  public editMode = false;
  public globalConf$ = this.getGlobalConf();

  constructor(private http: HttpClient) {}


  getGlobalConf():Observable<any> {
    return this.http.get<any>(this.baseUrl + "/get");
  }

  updateGlobalConf(globalConf: GlobalConf): Observable<any> {
    return this.http.put<any>(this.baseUrl + "/update", globalConf);
  }

}
