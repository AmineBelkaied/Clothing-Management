import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { DeliveryCompany } from '../models/DeliveryCompany';
import { DELIVERY_COMPANY_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class DeliveryCompanyService {

  private baseUrl: string = environment.baseUrl + `${DELIVERY_COMPANY_ENDPOINTS.BASE}`;
  public deliveryCompanySubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public deliveryCompany: BehaviorSubject<any> = new BehaviorSubject([]);
  public deliveryCompanyList: DeliveryCompany[] = [];
  public editMode = false;

  constructor(private http: HttpClient) {
    this.findAllDeliveryCompanies()
    .subscribe((deliveryCompanyList: any) => {
        this.deliveryCompanySubscriber.next(deliveryCompanyList);
        this.deliveryCompanyList = deliveryCompanyList.filter((deliveryCompany: any) => deliveryCompany.enabled);
    });
  }

  findAllDeliveryCompanies() {
    return this.http.get(`${this.baseUrl}`);
  }

  /*findDeliveryCompanyById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }*/

  addDeliveryCompany(deliveryCompany: DeliveryCompany) {
    return this.http.post(`${this.baseUrl}`, deliveryCompany , {observe: 'body'});
  }

  updateDeliveryCompany(deliveryCompany: DeliveryCompany) {
    return this.http.put(`${this.baseUrl}`, deliveryCompany , {headers : { 'content-type': 'application/json'}});
  }

  deleteDeliveryCompanyById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  loadDeliveryCompanies(){
    this.findAllDeliveryCompanies()
    .subscribe((deliveryCompanyList: any) => {
        this.deliveryCompanySubscriber.next(deliveryCompanyList);
        this.deliveryCompanyList = deliveryCompanyList.filter((deliveryCompany: any) => deliveryCompany.enabled);
    });
  }
  getDCSubscriber(): Observable<DeliveryCompany[]> {
    return this.deliveryCompanySubscriber.asObservable();
  }

  /*pushDeliveryCompany(deliveryCompany: DeliveryCompany){
    this.deliveryCompanyList.push(deliveryCompany);
  }*/

  spliceDeliveryCompany(updatedDeliveryCompany: any){
    let index = this.deliveryCompanyList.findIndex(deliveryCompany => deliveryCompany.id == updatedDeliveryCompany.id);
    this.deliveryCompanyList.splice(index , 1 , updatedDeliveryCompany);
  }

  editDeliveryCompany(updatedDeliveryCompany: DeliveryCompany) {
    this.deliveryCompany.next(updatedDeliveryCompany);
  }
}
