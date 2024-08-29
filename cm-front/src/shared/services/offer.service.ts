import { HttpClient, HttpParams } from '@angular/common/http';
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

  constructor(private http: HttpClient) { }

  loadOffers() : Observable<Offer[]> {
    return this.findAllOffersDTO().pipe(
      tap((offers: Offer[]) => {
        this.setOffers(offers);
      }),
      catchError((error) => {
        // Handle the error here
        console.error('Error fetching offers', error);
        return throwError(() => error);
      })
    )
  }

  findAllOffersDTO() : Observable<Offer[]>{
    return this.http.get<Offer[]>(`${this.baseUrl}`);
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

  setOffers(offers:Offer[]): void {
    this.offers = offers;
    this.offersSubscriber.next(offers);
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

  updatOfferFields(offer: Offer) {
    const params = new HttpParams().set('id', offer.id)
                                   .set('name', offer.name)
                                   .set('price', offer.price)
                                   .set('enabled', offer.enabled);
    return this.http.get(`${this.baseUrl}${OFFER_ENDPOINTS.UPDATE_DATA}`, {
      headers: { 'content-type': 'application/json' },
      params: params
    }).pipe(
      catchError((error) => {
        console.error('Error:', error);
        return throwError(error);
      })
    );
    //return this.http.put(`${this.baseUrl}${OFFER_ENDPOINTS.UPDATE_DATA}`+ ?id="+offer.id+"&name="+offer.name+"&price="+offer.price+"&enabled="+offer.enabled , {headers : { 'content-type': 'application/json'}})
  }

  updateOfferFbPages(offerId: number, fbPages: FbPage[]) {
    const params = new HttpParams().set('offerId', offerId)
    return this.http.put(`${this.baseUrl}${OFFER_ENDPOINTS.UPDATE_OFFER_FB_PAGES}`, fbPages ,
      {
        headers : { 'content-type': 'application/json'},
        params: params
      }).pipe(
        catchError((error) => {
          console.error('Error:', error);
          return throwError(error);
        })
      );
  }

  updateOfferModels(offerId: number, offerModelsDTO: OfferModelsDTO[]) : Observable<any> {
    const params = new HttpParams().set('offerId', offerId)
    return this.http.put(`${this.baseUrl}${OFFER_ENDPOINTS.UPDATE_OFFER_MODESLS}`, offerModelsDTO ,
      {
        headers : { 'content-type': 'application/json'},
        params: params
      }).pipe(
        catchError((error) => {
          console.error('Error:', error);
          return throwError(error);
        })
      );
  }

  deleteOfferById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedOffers(offersId: number[]) {
    return this.http.delete(`${this.baseUrl}${OFFER_ENDPOINTS.BATCH_DELETE}/${offersId}`);
  }

  spliceOffer(){
    let index = this.offers.findIndex(offer => offer.id == this.offer.id);
    this.offers.splice(index , 1 , this.offer);
    this.offersSubscriber.next(this.offers);
  }

  pushOffer(offer:any){
    this.offers.push(offer);
    this.offersSubscriber.next(this.offers);
  }

}
