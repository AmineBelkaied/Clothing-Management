import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { baseUrl } from '../../assets/constants';
import { Governorate } from '../models/Governorate';

@Injectable({
  providedIn: 'root'
})
export class GovernorateService {

  private baseUrl: string = baseUrl+"/governorate";

  constructor(private http: HttpClient) { }

  findAllGovernorates() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  findGovernorateById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addGovernorate(governorate: any) {
    return this.http.post(this.baseUrl + "/add", governorate, { observe: 'body' })
  }

  updateGovernorate(governorate: any) {
    return this.http.put(this.baseUrl + "/update", governorate, { headers: { 'content-type': 'application/json' } })
  }

  deleteGovernorateById(idGovernorate: any) {
    console.log(this.baseUrl + "/deleteById/" + idGovernorate)
    return this.http.delete(this.baseUrl + "/deleteById/" + idGovernorate)
  }

  deleteSelectedGovernorates(governoratesId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedGovernorates/" + governoratesId);
  }

}
