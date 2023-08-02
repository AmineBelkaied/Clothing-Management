import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Offer } from 'src/shared/models/Offer';
import { OfferModelDTO } from 'src/shared/models/OfferModelDTO';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class OfferService {

  private baseUrl: string = baseUrl+"/offer";
  public offers: OfferModelDTO[] = [];

  constructor(private http: HttpClient) { }

  findAllOffers() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  findOfferById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addOffer(OfferModelDTO: OfferModelDTO) {
    return this.http.post(this.baseUrl + "/add" , OfferModelDTO , {observe: 'body'})
  }

  updateOffer(OfferModelDTO: OfferModelDTO) {
    return this.http.put(this.baseUrl + "/update" , OfferModelDTO , {headers : { 'content-type': 'application/json'}})
  }

  deleteOfferById(idOffer: any) {
    console.log(this.baseUrl + "/deleteById/" + idOffer)
    return this.http.delete(this.baseUrl + "/deleteById/" + idOffer)
  }

  deleteSelectedOffers(offersId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedOffers/" + offersId);
  }
}
