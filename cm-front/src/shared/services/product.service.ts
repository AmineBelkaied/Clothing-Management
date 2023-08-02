import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl: string = baseUrl+"/product";

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

}
