import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl: string = "http://localhost:2233/product";

  constructor(private http: HttpClient) { }

  findAllProducts() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  deleteSelectedProducts(productsId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedProducts/" + productsId);
  }
}
