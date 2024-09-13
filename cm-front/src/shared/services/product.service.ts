import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { PRODUCT_ENDPOINTS } from '../constants/api-endpoints';
import { Product } from '../models/Product';
import { BehaviorSubject, map, Observable, tap } from 'rxjs';
import { ProductResponse } from '../models/ProductResponse';

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  public productsSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public productSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  private baseUrl: string = environment.baseUrl + `${PRODUCT_ENDPOINTS.BASE}`;
  products: ProductResponse[] = [];
  //allProducts: Product[];

  constructor(private http: HttpClient) { }

  loadProducts(): Observable<ProductResponse[]> {
    return this.findAllProducts().pipe(
      tap((products: ProductResponse[]) => {
        this.products = products;
        this.productsSubscriber.next(this.products);
      })
    );
  }
  loadProductsByModels(listModelIds: number[]): Observable<ProductResponse[]> {
    return this.findProductsByModels(listModelIds).pipe(
      tap((products: ProductResponse[]) => {
        this.updateProducts(products)
      })
    );
  }

  updateProducts(products: ProductResponse[]){
    products.forEach((product) => {
      // Check if the array and product id are properly defined
      if (this.products && product && product.id !== undefined) {
        // Find the index of the existing product with the same ID
        const index = this.products.findIndex((existingProduct) => existingProduct.id === product.id);

        if (index !== -1) {
          // Replace the existing product at the found index
          this.products[index] = product;
        } else {
          // If the product does not exist, add it to the array
          this.products.push(product);
        }
      }
    });

    this.productsSubscriber.next(this.products);
    console.log("this.products",this.products);

}

  getProductsSubscriber(): Observable<ProductResponse[]> {
    return this.productsSubscriber.asObservable();
  }

  findAllProducts(): Observable<ProductResponse[]>  {
    return this.http.get<ProductResponse[]>(`${this.baseUrl}`);
  }

  findProductsByModels(listModelIds : number[]): Observable<ProductResponse[]> {
    let modelIds = {'modelIds': listModelIds}
    return this.http.post<ProductResponse[]>(`${this.baseUrl}${PRODUCT_ENDPOINTS.MODEL_IDS}`, modelIds);
  }


  addStock(productsId: number[], qte: number, modelId: number, comment: string) {
    let updateStock = {'productsId': productsId, 'qte': qte, 'modelId': modelId, 'comment': comment}
    return this.http.post(`${this.baseUrl}${PRODUCT_ENDPOINTS.STOCK}`, updateStock);
  }

  updatProduct(product: any) {
    return this.http.put(`${this.baseUrl}`, product , {headers : {'content-type': 'application/json'}});
  }

  getStock(modelId: number,beginDate: string, endDate: string) {
    const params = new HttpParams()
                        .set('beginDate', beginDate)
                        .set('endDate', endDate);
    return this.http.get(`${this.baseUrl}${PRODUCT_ENDPOINTS.STOCK}/${modelId}`, {
      headers : {'content-type': 'application/json'},
      params : params
      });
  }

  deleteSelectedProducts(productsId: number[]) {
    return this.http.delete(`${this.baseUrl}${PRODUCT_ENDPOINTS.BATCH_DELETE}/${productsId}`);
  }
}
