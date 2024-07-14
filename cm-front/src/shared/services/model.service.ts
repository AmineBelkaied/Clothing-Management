import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Model } from 'src/shared/models/Model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  private baseUrl: string = environment.baseUrl + "/model";

  constructor(private http: HttpClient) { }

  findAllModels() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  findModelById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addModel(model: Model) {
    return this.http.post(this.baseUrl + "/add", model, { observe: 'body' })
  }

  updateModel(model: Model) {
    return this.http.put(this.baseUrl + "/update", model, { headers: { 'content-type': 'application/json' } })
  }

  deleteModelById(idModel: any) {
    console.log(this.baseUrl + "/deleteById/" + idModel)
    return this.http.delete(this.baseUrl + "/deleteById/" + idModel)
  }

  deleteSelectedModels(modelsId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedModels/" + modelsId);
  }
}
