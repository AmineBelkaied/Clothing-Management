import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { PRODUCT_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  private baseUrl: string = environment.baseUrl + `${PRODUCT_ENDPOINTS.BASE}`;

  constructor(private http: HttpClient) { }

  findAllProducts() {
    return this.http.get(`${this.baseUrl}`);
  }

  addStock(productsId: number[], qte: number, modelId: number, comment: string) {
    let updateStock = {'productsId': productsId, 'qte': qte, 'modelId': modelId, 'comment': comment}
    return this.http.post(`${this.baseUrl}${PRODUCT_ENDPOINTS.STOCK}`, updateStock);
  }

  updatProduct(product: any) {
    return this.http.put(`${this.baseUrl}`, product , {headers : {'content-type': 'application/json'}});
  }

  getStock(modelId: number) {
    return this.http.get(`${this.baseUrl}${PRODUCT_ENDPOINTS.STOCK}/${modelId}`);
  }

  deleteSelectedProducts(productsId: number[]) {
    return this.http.delete(`${this.baseUrl}/${PRODUCT_ENDPOINTS.BATCH_DELETE}/${productsId}`);
  }
}
