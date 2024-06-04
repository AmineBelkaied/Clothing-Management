import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl: string = baseUrl + "/product";

  constructor(private http: HttpClient) { }

  findAllProducts() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  deleteSelectedProducts(productsId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedProducts/" + productsId);
  }

  updatProduct(product: any) {
    return this.http.put(this.baseUrl + "/update", product, { headers: { 'content-type': 'application/json' } })
  }

  getStock(modelId: number) {
    return this.http.get(this.baseUrl + "/getStock/" + modelId);
  }

  addStock(productsId: number[],qte:number,modelId:number,comment:String) {
    let updateStock = {'productsId': productsId,'qte':qte,'modelId':modelId,'comment':comment}
    return this.http.post(this.baseUrl + "/addStock", updateStock);
  }
}
