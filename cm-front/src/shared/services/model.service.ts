import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Model } from 'src/shared/models/Model';
import { environment } from '../../environments/environment';
import { MODEL_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  private baseUrl: string = environment.baseUrl + `${MODEL_ENDPOINTS.BASE}`;

  constructor(private http: HttpClient) { }

  findAllModels() {
    return this.http.get(`${this.baseUrl}`);
  }

  findModelById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addModel(model: Model) {
    return this.http.post(`${this.baseUrl}`, model , {observe: 'body'});
  }

  updateModel(model: Model) {
    return this.http.put(`${this.baseUrl}`, model , {headers : {'content-type': 'application/json'}});
  }

  deleteModelById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedModels(modelsId: number[]) {
    return this.http.delete(`${this.baseUrl}/${MODEL_ENDPOINTS.BATCH_DELETE}/${modelsId}`);
  }
}
