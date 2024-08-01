import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { baseUrl } from '../../assets/constants';
import { DeliveryCompany } from '../models/DeliveryCompany';

@Injectable({
  providedIn: 'root'
})
export class SteLivraisonService {

  private baseUrl: string = baseUrl+"/deliveryCompany";
  public deliveryCompanySubscriber: BehaviorSubject<DeliveryCompany[]> = new BehaviorSubject<DeliveryCompany[]>([]);
  public deliveryCompany: BehaviorSubject<any> = new BehaviorSubject([]);
  public deliveryCompanyList: DeliveryCompany[] = [];
  public editMode = false;
  constructor(private http: HttpClient) {

  }
  loadDeliveryCompanies(): void {
    this.findAllStes().subscribe({
      next: (deliveryCompanyList: DeliveryCompany[]) => {
        this.deliveryCompanySubscriber.next(deliveryCompanyList);
        this.deliveryCompanyList = deliveryCompanyList.filter((deliveryCompany: DeliveryCompany) => deliveryCompany.deleted);
      },
      error: (error) => {
        console.error('Error fetching delivery companies', error);
      }
    });
  }

  getDCSubscriber(): Observable<DeliveryCompany[]> {
    // Ensure the data is loaded if not already
    if (this.deliveryCompanySubscriber.value.length === 0) {
      this.loadDeliveryCompanies();
    }
    return this.deliveryCompanySubscriber.asObservable();
  }

  findAllStes() : Observable<DeliveryCompany[]>{
    return this.http.get<DeliveryCompany[]>(this.baseUrl + "/findAll");
  }

  findSteById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addSte(deliveryCompany: DeliveryCompany) {
    return this.http.post(this.baseUrl + "/add" , deliveryCompany , {observe: 'body'})
  }

  updateSte(deliveryCompany: DeliveryCompany) {
    return this.http.put(this.baseUrl + "/update" , deliveryCompany , {headers : { 'content-type': 'application/json'}})
  }

  deleteSteById(idDeliveryCompany: any) {
    return this.http.delete(this.baseUrl + "/deleteById/" + idDeliveryCompany)
  }

  pushSte(deliveryCompany: DeliveryCompany){
    this.deliveryCompanyList.push(deliveryCompany);
  }

  spliceSte(updatedSte: any){
    let index = this.deliveryCompanyList.findIndex(deliveryCompany => deliveryCompany.id == updatedSte.id);
    console.log(index);
    this.deliveryCompanyList.splice(index , 1 , updatedSte);
  }

  editSte(updatedSte: DeliveryCompany) {
    this.deliveryCompany.next(updatedSte);
  }
}
