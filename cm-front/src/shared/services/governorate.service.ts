import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { GOVERNORATE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class GovernorateService {

  private baseUrl: string = environment.baseUrl + `${GOVERNORATE_ENDPOINTS.BASE}`;

  constructor(private http: HttpClient) { }

  findAllGovernorates() {
    return this.http.get(`${this.baseUrl}`);
  }

  findGovernorateById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addGovernorate(governorate: any) {
    return this.http.post(`${this.baseUrl}`, governorate , {observe: 'body'});
  }

  updateGovernorate(governorate: any) {
    return this.http.put(`${this.baseUrl}`, governorate , {headers : { 'content-type': 'application/json'}});
  }

  deleteGovernorateById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedGovernorates(governoratesId: number[]) {
    return this.http.delete(`${this.baseUrl}/${GOVERNORATE_ENDPOINTS.BATCH_DELETE}/${governoratesId}`);
  }

}
