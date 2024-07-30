import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Model } from 'src/shared/models/Model';
import { baseUrl } from '../../assets/constants';
import { BehaviorSubject, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ModelService {

  private baseUrl: string = baseUrl+"/model";
  public modelSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  model : Model;
  constructor(private http: HttpClient) { }

  setModel(model:any){
    this.model = model;
    this.modelSubscriber.next(model);
  }


  getmodelSubscriber(): Observable<Model> {
    return this.modelSubscriber.asObservable();
  }

  findAllModelsDTO() {
    return this.http.get(this.baseUrl + "/modelsDTO");
  }

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
