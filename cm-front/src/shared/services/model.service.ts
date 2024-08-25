import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Model } from 'src/shared/models/Model';
import { environment } from '../../environments/environment';
import { MODEL_ENDPOINTS } from '../constants/api-endpoints';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ModelService {

  private baseUrl: string = environment.baseUrl + `${MODEL_ENDPOINTS.BASE}`;
  model : Model;

  defaultModel: Model = {
    id: 0, // Or any default value
    name: '',
    description: '',
    colors: [] =[],
    sizes: [] = [],
    products: [] = [],
    earningCoefficient: 2,
    purchasePrice: 15,
    deleted: false,
    enabled: false
  };

  public modelSubscriber: BehaviorSubject<Model> = new BehaviorSubject<Model>(
    this.defaultModel
  );

  public modelsSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  models: Model[] = [];

  constructor(private http: HttpClient) { }

  loadModels(): Observable<Model[]> {
    return this.findAllModelsDTO().pipe(
      tap((models: Model[]) => this.setModels(models)),
      catchError((error) => {
        // Handle the error here
        console.error('Error fetching offers', error);
        return throwError(() => error);
      })
    );
  }

  findAllModelsDTO(): Observable<Model[]> {
    console.log("findAllModelsDTO");
    return this.http.get<Model[]>(`${this.baseUrl}`);
  }

  getModelsSubscriber(): Observable<Model[]> {
    if (this.modelsSubscriber.value.length === 0) {
      this.loadModels().subscribe();
    }
    return this.modelsSubscriber.asObservable();
  }



  setModel(model: Model) {
    this.model = model;
    this.modelSubscriber.next(model);
  }

  setModels(models: Model[]) {
    this.models = models;
    this.modelsSubscriber.next(models);
  }

  cleanModel() {
    console.log("cleanModel");
    this.model = this.defaultModel;
    this.modelSubscriber.next(this.defaultModel);
  }

  updateModelsSubscriber(model: Model) {
    const index = this.findModelIndexById(model.id!);
    if (index !== -1) {
      this.models[index] = this.model;
      this.modelSubscriber.next(this.defaultModel);
    } else {
      this.models.push(this.model);
    }
    this.modelsSubscriber.next(this.models);
  }

  findModelIndexById(modelId: number): number {
    let index = -1;
    for (let i = 0; i < this.models.length; i++) {
      if (this.models[i].id === modelId) {
        index = i;
        break;
      }
    }
    return index;
  }

  getModelSubscriber(): Observable<Model> {
    return this.modelSubscriber.asObservable();
  }

  findAllModels() {
    return this.http.get(`${this.baseUrl}` + '/find-all');
  }

  findModelById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addModel(model: Model): Observable<Model>{
    return this.http.post<Model>(`${this.baseUrl}`, model , {observe: 'body'});
  }

  updateModel(model: Model) {
    return this.http.put(`${this.baseUrl}`, model , {headers : {'content-type': 'application/json'}});
  }

  deleteModelById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedModels(modelsId: number[]) {
    return this.http.delete(`${this.baseUrl}${MODEL_ENDPOINTS.BATCH_DELETE}/${modelsId}`);
  }
}
