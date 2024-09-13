import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
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

  constructor(private http: HttpClient) {
    this.getDeliveryCompaniesSubscriber();
  }

  findAllDeliveryCompanies(): Observable<DeliveryCompany[]> {
    return this.http.get<DeliveryCompany[]>(`${this.baseUrl}`);
  }

  getDeliveryCompaniesSubscriber():Observable<DeliveryCompany[]>{
    if (this.deliveryCompanyListSubscriber.value.length === 0) {
      this.loadDeliveryCompanies().subscribe();
    }
    return this.deliveryCompanyListSubscriber.asObservable();
  }

  loadDeliveryCompanies(): Observable<DeliveryCompany[]>{
    return this.findAllDeliveryCompanies().pipe(
    tap((deliveryCompanyList: DeliveryCompany[]) =>
      {
        this.deliveryCompanyListSubscriber.next(deliveryCompanyList);
        this.deliveryCompanyList = deliveryCompanyList;
      }
    ),
    catchError((error) => {
      console.error('Error fetching delivery company', error);
      return throwError(() => error);
    }))
  }
  addDeliveryCompany(deliveryCompany: DeliveryCompany) {
    return this.http.post(`${this.baseUrl}`, deliveryCompany , {observe: 'body'});
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
}
