import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { PRODUCT_HISTORY_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class ProductHistoryService {

  private baseUrl: string = environment.baseUrl + `${PRODUCT_HISTORY_ENDPOINTS.BASE}`;

  constructor(private http: HttpClient) { }

  findAllProductsHistory(
    modelId: any,
    page: number,
    size: number,
    colorSize?: string,
    beginDate?: any,
    endDate?: any
  ): Observable<any> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (colorSize) {
      params = params.set('colorSize', colorSize);
    }

    if (beginDate) {
      params = params.set('beginDate', beginDate);
      params = params.set('endDate', endDate || beginDate);
    }

    return this.http.get(`${this.baseUrl}${PRODUCT_HISTORY_ENDPOINTS.BY_MODEL}/${modelId}`, { params });
  }

  findAll(modelId: any, page:number, colorSize?: string, beginDate?: any, endDate?: any): Observable<any> {
    let params = new HttpParams();
    if(page>0)
    params = params.set('page', page);

    if (colorSize) {
      params = params.set('colorSize', colorSize);
    }

    if (beginDate) {
      params = params.set('beginDate', beginDate);
      params = params.set('endDate', endDate || beginDate);
    }

    return this.http.get(`${this.baseUrl}${PRODUCT_HISTORY_ENDPOINTS.BY_MODEL}/${modelId}`, { params });
  }

  addProductsHistory(productHistory: any[]): Observable<any> {
    return this.http.post(`${this.baseUrl}`, productHistory);
  }

  updatProductHistory(productHistory: any): Observable<any> {
    return this.http.put(`${this.baseUrl}`, productHistory, { headers: { 'content-type': 'application/json' } })
  }

  deleteProductsHistory(productsHistory: any[], modelId: number, page: number): Observable<any> {
    return this.http.post(`${this.baseUrl}${PRODUCT_HISTORY_ENDPOINTS.BATCH_DELETE}/${modelId}?page=${page}`, productsHistory);
  }
}
