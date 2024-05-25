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

  findAllProductsHistory(modelId: any, page: number, size: number, reference?: string, beginDate?: any, endDate?: any): Observable<any> {
    if(beginDate == undefined)
      beginDate = "";
    if(endDate == undefined)
    endDate = beginDate;
    if(reference == undefined)
      reference = "";
    return this.http.get(this.baseUrl + "/findAllByModelId/" + modelId + "?page=" + page + "&size=" + size + "&reference=" + reference + "&beginDate=" + beginDate + "&endDate=" + endDate);
  }

  findAll(modelId: any, reference?: string, beginDate?: any, endDate?: any): Observable<any> {
    if(beginDate == undefined)
      beginDate = "";
    if(endDate == undefined)
      endDate = beginDate;
    if(reference == undefined)
      reference = "";
    return this.http.get(this.baseUrl + "/findAllByModelId/" + modelId + "?beginDate=" + beginDate + "&endDate=" + endDate + "&reference=" + reference);
  }

  addProductsHistory(productHistory: any[]): Observable<any> {
    return this.http.post(this.baseUrl + "/addProductsHistory", productHistory);
  }

  updatProductHistory(productHistory: any): Observable<any> {
    return this.http.put(this.baseUrl + "/updateProductHistory", productHistory, { headers: { 'content-type': 'application/json' } })
  }

  deleteProductsHistory(productsHistory: any[],modelId: number, page: number): Observable<any> {
    return this.http.post(this.baseUrl + "/deleteProductsHistory/"+ modelId + "?page=" + page , productsHistory);
  }
}