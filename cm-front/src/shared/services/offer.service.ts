import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { baseUrl } from '../../assets/constants';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { Offer } from '../models/Offer';
import { OfferModelsDTO } from '../models/OfferModelsDTO';
import { FbPage } from '../models/FbPage';

@Injectable({
  providedIn: 'root'
})
export class OfferService {
  public offersSubscriber: BehaviorSubject<any> = new BehaviorSubject<Offer[]>([]);
  public offerSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public offers: Offer[] = [];
  public offer: Offer;
  private baseUrl: string = baseUrl+"/offer";
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

  setOffer(offer:any): void {
    this.offer = offer;
    this.offerSubscriber.next(offer);
  }

  findAllOffers() {
    return this.http.get(this.baseUrl + "/findAll");
  }


  findOffersByFbPageId(id: number) : Observable<Offer[]>{
    return this.http.get<Offer[]>(this.baseUrl + "/findByFBPage/"+id);
  }

  findAllOffersModelQuantities() {
    return this.http.get(this.baseUrl + "/findAllOffersModelQuantities");
  }
  findOffersModelQuantitiesById(id : number) {
    return this.http.get(this.baseUrl + "/findOffersModelQuantitiesById/"+id);
  }

  findOfferById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addOffer(offer: Offer) {
    return this.http.post(this.baseUrl + "/add" , offer , {observe: 'body'})
  }

  updateOffer(offer: Offer) {
    return this.http.post(this.baseUrl + "/updateData?id="+offer.id+"&name="+offer.name+"&price="+offer.price+"&enabled="+offer.enabled , {headers : { 'content-type': 'application/json'}})
  }

  updateOfferFbPages(offerId: number, fbPages: FbPage[]) {
    return this.http.post(this.baseUrl + "/updateOfferFbPages?offerId="+offerId , fbPages , {headers : { 'content-type': 'application/json'}})
  }

  updateOfferModels(offerId: number,OfferModelsDTO: OfferModelsDTO[]) : Observable<any> {
    return this.http.post(this.baseUrl + "/updateOfferModels?offerId="+offerId , OfferModelsDTO , {headers : { 'content-type': 'application/json'}})
  }

  deleteOfferById(idOffer: any) {
    console.log(this.baseUrl + "/deleteById/" + idOffer)
    return this.http.delete(this.baseUrl + "/deleteById/" + idOffer)
  }

  deleteSelectedOffers(offersId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedOffers/" + offersId);
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
