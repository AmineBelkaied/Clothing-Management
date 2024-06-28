import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { Offer } from '../models/Offer';
import { OfferModelsDTO } from '../models/OfferModelsDTO';
import { FbPage } from '../models/FbPage';
import { environment } from '../../environments/environment';
import { OFFER_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  public offersSubscriber: BehaviorSubject<any> = new BehaviorSubject<Offer[]>([]);
  public offerSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public offers: Offer[] = [];
  public offer: Offer;
  private baseUrl: string = environment.baseUrl + `${OFFER_ENDPOINTS.BASE}`;
  //public offersDTO: Offer[] = [];

  constructor(private http: HttpClient) { }

  loadOffers() : Observable<Offer[]> {
    return this.findAllOffersDTO().pipe(
      tap((offers: Offer[]) => {
        this.offers = offers;
        this.offersSubscriber.next(this.offers);
      }),
      catchError((error) => {
        // Handle the error here
        console.error('Error fetching offers', error);
        return throwError(() => error);
      })
    )
  }
  findAllOffersDTO() : Observable<Offer[]>{
    return this.http.get<Offer[]>(this.baseUrl + "/offersDTO");
  }

  getOffersSubscriber(): Observable<Offer[]> {
    if (this.offersSubscriber.value.length === 0) {
      this.loadOffers().subscribe();
    }
    return this.offersSubscriber.asObservable();
  }

cleanOffre() {
   // this.offer = this.defaultModel;
    this.offerSubscriber=  new BehaviorSubject([]);
  }
  setOffer(offer:any): void {
    this.offer = offer;
    this.offerSubscriber.next(offer);
  }

  findAllOffers() {
    return this.http.get(`${this.baseUrl}`);
  }


  findOffersByFbPageId(id: number) : Observable<Offer[]>{
    return this.http.get<Offer[]>(`${this.baseUrl}${OFFER_ENDPOINTS.BY_FB_PAGE}/${id}`);
  }

  findAllOffersModelQuantities() {
    return this.http.get(`${this.baseUrl}${OFFER_ENDPOINTS.MODEL_QUANTITIES}`);
  }

  findOffersModelQuantitiesById(id : number) {
    return this.http.get(this.baseUrl + "/findOffersModelQuantitiesById/"+id);
  }

  findOfferById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addOffer(offer: Offer) {
    return this.http.post(`${this.baseUrl}` , offer , {observe: 'body'})
  }

  updateOffer(offer: Offer) {
    return this.http.put(`${this.baseUrl}`, offer , {headers : {'content-type': 'application/json'}});
  }

  updateData(offer: Offer) {
    return this.http.put(this.baseUrl + "/update-data?id="+offer.id+"&name="+offer.name+"&price="+offer.price+"&enabled="+offer.enabled , {headers : { 'content-type': 'application/json'}})
  }

  updateOfferFbPages(offerId: number, fbPages: FbPage[]) {
    return this.http.put(this.baseUrl + "/update-offer-fb-pages?offerId="+offerId , fbPages , {headers : { 'content-type': 'application/json'}})
  }

  updateOfferModels(offerId: number, offerModelsDTO: OfferModelsDTO[]) : Observable<any> {
    return this.http.put(this.baseUrl + "/update-offer-models?offerId=" + offerId , offerModelsDTO , {headers : { 'content-type': 'application/json'}})
  }

  deleteOfferById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedOffers(offersId: number[]) {
    return this.http.delete(`${this.baseUrl}/${OFFER_ENDPOINTS.BATCH_DELETE}/${offersId}`);
  }

  spliceOffer(){
    console.log('this.offer',this.offer);
    console.log('this.offers0',this.offers);
    let index = this.offers.findIndex(offer => offer.id == this.offer.id);
    console.log('index',index);
    this.offers.splice(index , 1 , this.offer);
    console.log('this.offers',this.offers);
    this.offersSubscriber.next(this.offers);
  }

  pushOffer(offer:any){
    this.offers.push(offer);
    this.offersSubscriber.next(this.offers);
  }

}
