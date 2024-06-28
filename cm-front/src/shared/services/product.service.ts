import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { PRODUCT_ENDPOINTS } from '../constants/api-endpoints';
import { Product } from '../models/Product';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  public productsSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public productSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  private baseUrl: string = environment.baseUrl + `${PRODUCT_ENDPOINTS.BASE}`;
  products: Product[];

  constructor(private http: HttpClient) { }

  loadProducts(): Observable<Product[]> {
    return this.findAllProducts().pipe(
      map((products: any) => products.filter((product: Product) => !product.deleted)),
      tap((products: Product[]) => {
        this.products = products;
        //console.log("this.products", this.products);
        this.productsSubscriber.next(this.products);
      })
    );
  }

  getProductsSubscriber(): Observable<Product[]> {
    return this.productsSubscriber.asObservable();
  }

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
