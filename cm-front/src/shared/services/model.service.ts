import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Model } from 'src/shared/models/Model';
import { baseUrl } from '../../assets/constants';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root',
})
export class ModelService {
  defaultModel: Model = {
    id: 0, // Or any default value
    name: '',
    description: '',
    colors: [],
    sizes: [],
    products: [],
    earningCoefficient: 2,
    purchasePrice: 15,
    deleted: false,
  };
  private baseUrl: string = baseUrl + '/model';
  public modelsSubscriber: BehaviorSubject<Model[]> = new BehaviorSubject<
    Model[]
  >([]);
  public modelSubscriber: BehaviorSubject<Model> = new BehaviorSubject<Model>(
    this.defaultModel
  );
  model: Model;
  models: Model[];

  constructor(
    private http: HttpClient,
    private messageService: MessageService
  ) {}

  loadModels(): Observable<Model[]> {
    return this.findAllModelsDTO().pipe(
      tap((models: Model[]) => {
        this.models = models;
        this.modelsSubscriber.next(this.models);
      }),
      catchError((error) => {
        // Handle the error here
        console.error('Error fetching offers', error);
        return throwError(() => error);
      })
    );
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
  cleanModel() {
    console.log("cleanModel");
    this.model = this.defaultModel;
    this.modelSubscriber.next(this.defaultModel);
  }
  updateModelsSubscriber(model: Model) {
    const index = this.findModelIndexById(model.id!);
    console.log("updateModelsSubscriber",index);
    if (index !== -1) {
      this.models[index] = this.model;
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

  findAllModelsDTO(): Observable<Model[]> {
    return this.http.get<Model[]>(this.baseUrl + '/modelsDTO');
  }

  findAllModels() {
    return this.http.get(this.baseUrl + '/findAll');
  }

  findModelById(id: number) {
    return this.http.get(this.baseUrl + '/findById/' + id);
  }

  saveModel(model: Model): Observable<Model> {
    return this.http.post<Model>(this.baseUrl + '/save', model, {
      headers: { 'content-type': 'application/json' },
    });
  }

  deleteModelById(idModel: any) {
    console.log(this.baseUrl + '/deleteById/' + idModel);
    return this.http.delete(this.baseUrl + '/deleteById/' + idModel);
  }

  deleteSelectedModels(modelsId: any[]) {
    return this.http.delete(this.baseUrl + '/deleteSelectedModels/' + modelsId);
  }
}
