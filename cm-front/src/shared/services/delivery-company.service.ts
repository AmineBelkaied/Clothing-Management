import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, shareReplay, take, tap, throwError } from 'rxjs';
import { environment } from '../../environments/environment';
import { DeliveryCompany } from '../models/DeliveryCompany';
import { DELIVERY_COMPANY_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class DeliveryCompanyService {

  private baseUrl: string = environment.baseUrl + `${DELIVERY_COMPANY_ENDPOINTS.BASE}`;
  public deliveryCompanyListSubscriber:  BehaviorSubject<DeliveryCompany[]> = new BehaviorSubject<DeliveryCompany[]>([]);
  public deliveryCompanySubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public deliveryCompany: BehaviorSubject<any> = new BehaviorSubject([]);
  public deliveryCompanyList: DeliveryCompany[] = [];
  public editMode = false;
  private deliveryCompaniesCache$: Observable<DeliveryCompany[]> | undefined;

  constructor(private http: HttpClient) {
  }

  findAllDeliveryCompanies(): Observable<DeliveryCompany[]> {
    return this.http.get<DeliveryCompany[]>(`${this.baseUrl}`);
  }

  getDeliveryCompaniesSubscriber():Observable<DeliveryCompany[]>{
    if (this.deliveryCompanyListSubscriber.value.length === 0) {
      this.loadDeliveryCompanies();
    }
    return this.deliveryCompanyListSubscriber.asObservable();
  }

  loadDeliveryCompanies(): void {
    if (!this.deliveryCompaniesCache$) {
      this.deliveryCompaniesCache$ = this.findAllDeliveryCompanies().pipe(
        take(1),
        tap((deliveryCompanyList: DeliveryCompany[]) =>
          {
            this.deliveryCompanyListSubscriber.next(deliveryCompanyList);
            this.deliveryCompanyList = deliveryCompanyList;
          }
        ),
        catchError((error) => {
          console.error('Error fetching delivery company', error);
          return throwError(() => error);
        }),shareReplay(1)
      )
    }
    this.deliveryCompaniesCache$.subscribe();
  }


  addDeliveryCompany(deliveryCompany: DeliveryCompany) {
    return this.http.post(`${this.baseUrl}`, deliveryCompany , {observe: 'body'});
  }

  getJaxStatus() {
    let jax= "https://core.jax-delivery.com/api/user/colis/all?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL2NvcmUuamF4LWRlbGl2ZXJ5LmNvbS9hcGkvY2xpZW50L2xvZ2luIiwiaWF0IjoxNzI5MzYxMzMzLCJleHAiOjE3NjU2NDkzMzMsIm5iZiI6MTcyOTM2MTMzMywianRpIjoiYXNZejJxd1FkR1BwSWdtVyIsInN1YiI6IjE2NzciLCJwcnYiOiJkMDkwNWJjZjY1YTZkOTkyZDkwY2JmZTQ2MjI2YmQzMTNhZTUxOTNmIn0.HmVJusi_0DtMYntj3gBADSDEuG6OLrwofOv4FZ1xNt8&code=SOU290207831&id=0&nbr=0&page=0"

    return this.http.get(jax);
  }

  updateDeliveryCompany(deliveryCompany: DeliveryCompany) {
    return this.http.put(`${this.baseUrl}`, deliveryCompany , {headers : { 'content-type': 'application/json'}});
  }

  deleteDeliveryCompanyById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  spliceDeliveryCompany(updatedDeliveryCompany: any){
    let index = this.deliveryCompanyList.findIndex(deliveryCompany => deliveryCompany.id == updatedDeliveryCompany.id);
    if(index>-1)
      this.deliveryCompanyList.splice(index , 1 , updatedDeliveryCompany);
    else this.deliveryCompanyList.push(updatedDeliveryCompany);
    this.deliveryCompanySubscriber.next(this.deliveryCompanyList);
  }

  editDeliveryCompany(updatedDeliveryCompany: DeliveryCompany) {
    this.deliveryCompany.next(updatedDeliveryCompany);
  }

  getDeliveryCompanyById(id: number): DeliveryCompany | undefined {
    // Using the find method to locate the first matching color by its id.
    return this.deliveryCompanyList.find(deliveryCompany => deliveryCompany.id === id);
  }
}
