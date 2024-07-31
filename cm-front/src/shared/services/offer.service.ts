import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { OfferModelDTO } from 'src/shared/models/OfferModelDTO';
import { environment } from '../../environments/environment';
import { OFFER_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class OfferService {

  private baseUrl: string = environment.baseUrl + `${OFFER_ENDPOINTS.BASE}`;
  public offers: OfferModelDTO[] = [];

  constructor(private http: HttpClient) { }

  findAllOffers() {
    return this.http.get(`${this.baseUrl}`);
  }

  findOffersByFbPageId(id: number) {
    return this.http.get(`${this.baseUrl}${OFFER_ENDPOINTS.BY_FB_PAGE}/${id}`);
  }

  findAllOffersModelQuantities() {
    return this.http.get(`${this.baseUrl}${OFFER_ENDPOINTS.MODEL_QUANTITIES}`);
  }

  findOfferById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addOffer(offerModelDTO: OfferModelDTO) {
    return this.http.post(`${this.baseUrl}`, offerModelDTO, { observe: 'body' });
  }

  updateOffer(offerModelDTO: OfferModelDTO) {
    return this.http.put(`${this.baseUrl}`, offerModelDTO, { headers: { 'content-type': 'application/json' }});
  }

  deleteOfferById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedOffers(offersId: number[]) {
    return this.http.delete(`${this.baseUrl}/${OFFER_ENDPOINTS.BATCH_DELETE}/${offersId}`);
  }
}
