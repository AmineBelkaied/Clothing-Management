import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Packet } from '../models/Packet';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { TO_VERIFY, CONFIRMED, IN_PROGRESS, IN_PROGRESS_1, IN_PROGRESS_2, IN_PROGRESS_3, PAID, RETURN_RECEIVED, RETURN, DELIVERED } from '../utils/status-list';
import { STAT_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class StatsService {

  constructor(private http: HttpClient) {
  }

  private baseUrl: string = environment.baseUrl + `${STAT_ENDPOINTS.BASE}`;

  public productsCount(modelId: number, beginDate: string, endDate: string): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate);

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.SOLD}/${modelId}`, { params });
  }

  public statAllModels(beginDate: string, endDate: string, countProgress: boolean): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate)
      .set('countProgress', countProgress);

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.MODELS}`, { params });
  }

  public statStock(beginDate: string, endDate: string): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate);

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.STOCK}`, { params });
  }

  public statAllPackets(beginDate: string, endDate: string, deliveryCompanyName: string): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate)
      .set('deliveryCompanyName', deliveryCompanyName);

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.PACKETS}`, { params });
  }

  public statAllColors(beginDate: string, endDate: string, modelIds: number[]): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate)
      .set('modelIds', modelIds.join(','));

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.COLORS}`, { params });
  }

  public statAllOffers(beginDate: string, endDate: string): Observable<any> {
    let params = new HttpParams()
    .set('beginDate', beginDate)
    .set('endDate', endDate);

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.OFFERS}`, { params });
  }

  public statModelSold(modelId: number, beginDate: string, endDate: string): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate);

    return this.http.get(`${this.baseUrl}${STAT_ENDPOINTS.MODEL}/${modelId}`, { params });
  }

  getStatsTreeNodesData(data: Packet[]) {

    interface CountCity { [cityName: string]: { count: number, confirm: number, citys: { [name: string]: { count: number, confirm: number } } } }
    interface CountPage { [pageName: string]: { count: number, confirm: number } }
    interface CountDate { [date: string]: { count: number, payed: number, return: number, exchange: number, out: number } }
    interface Count { cityCounts: CountCity, pageCounts: CountPage, dateCounts: CountDate }

    let cityCounts: CountCity = {};
    let pageCounts: CountPage = {};
    let dateCounts: CountDate = {};
    data.forEach((packet) => {
      //count pages
      if (
        packet.fbPage != undefined
      ) {
        if (pageCounts[packet.fbPage?.name]) {
          pageCounts[packet.fbPage?.name].count++;
        }
        else {
          pageCounts[packet.fbPage?.name] = { count: 1, confirm: 0 };
        }
      }

      if (
        packet.date != undefined
      ) {
        let date = packet.date.substring(0, 10)
        if (dateCounts[date]) {
          dateCounts[date].count++;
        }
        else {
          dateCounts[date] = { count: 1, payed: 0, return: 0, exchange: 0, out: 0 };
        }
        if (packet.status == PAID || packet.status == DELIVERED) {
          dateCounts[date].payed++;
        }
        else if (packet.status == RETURN || packet.status == RETURN_RECEIVED) {
          dateCounts[date].return++;
        }

        if (packet.status == CONFIRMED || packet.status == PAID || packet.status == DELIVERED
          || packet.status == RETURN || packet.status == RETURN_RECEIVED || packet.status == TO_VERIFY
          || packet.status == IN_PROGRESS_1 || packet.status == IN_PROGRESS_2 || packet.status == IN_PROGRESS_3 || packet.status == IN_PROGRESS
        ) {
          //|| packet.status.substring(0,7) == 'En Cours'
          if (packet.exchangeId) {
            dateCounts[date].exchange++;
          }
          dateCounts[date].out++;
        }
      }


      if (
        packet.city != undefined &&
        packet.city.name != undefined &&
        packet.city.governorate?.name != undefined
      ) {
        if (cityCounts[packet.city?.governorate.name]) {
          cityCounts[packet.city?.governorate.name].count++;
          if (cityCounts[packet.city?.governorate.name].citys[packet.city.name]) cityCounts[packet.city?.governorate.name].citys[packet.city.name].count++;
          else {
            cityCounts[packet.city?.governorate.name].citys[packet.city.name] = { count: 1, confirm: 0 };
          }
        } else {
          cityCounts[packet.city?.governorate.name] = { count: 1, confirm: 0, citys: {} };
          cityCounts[packet.city?.governorate.name].citys[packet.city.name] = { count: 1, confirm: 0 };
        }
        if (packet.status == PAID || packet.status == DELIVERED) {
          cityCounts[packet.city?.governorate.name].confirm++;
          cityCounts[packet.city?.governorate.name].citys[packet.city.name].confirm++;
        }
      }
    });
    let count: Count = { cityCounts, pageCounts, dateCounts }
    console.log(count);

    return count;
  }

}
export interface DaySales {
  day: number;
  model: Model[];
}
export interface Model {
  modelName: string;
  modelColors: Colors[];
}
export interface Colors {
  colorName: String;
  sizes: Sizes[];
}
export interface Sizes {
  sizeName: String;
  qte: number;
}

