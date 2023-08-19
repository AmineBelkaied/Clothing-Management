import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { baseUrl } from 'src/assets/constants';

@Injectable({
  providedIn: 'root'
})
export class ProductHistoryService {

  private baseUrl: string = baseUrl + "/productHistory";

  constructor(private http: HttpClient) { }

  findAllProductsHistory(modelId: any, page: number, size: number): Observable<any> {
    return this.http.get(this.baseUrl + "/findAllByModelId/" + modelId + "?page=" + page + "&size=" + size);
  }

  findAll(modelId: any): Observable<any> {
    return this.http.get(this.baseUrl + "/findAllByModelId/" + modelId);
  }

  addProductsHistory(productHistory: any[]): Observable<any> {
    return this.http.post(this.baseUrl + "/addProductsHistory", productHistory);
  }

  updatProductHistory(productHistory: any): Observable<any> {
    return this.http.put(this.baseUrl + "/updateProductHistory", productHistory, { headers: { 'content-type': 'application/json' } })
  }

}
