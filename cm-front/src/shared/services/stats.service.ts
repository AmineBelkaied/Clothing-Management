import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Packet } from '../models/Packet';
import { BehaviorSubject, Observable, Subject, tap, throwError } from 'rxjs';
import { baseUrl } from '../../assets/constants';
import { DateUtils } from '../utils/date-utils';
import { A_VERIFIER, CONFIRMEE, EN_COURS, EN_COURS_1, EN_COURS_2, EN_COURS_3, LIVREE, PAYEE, RETOUR, RETOUR_RECU } from '../utils/status-list';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  constructor(private http: HttpClient,private dateUtils: DateUtils) {
  }
  //cityTree? : {name:string,occ:number}[];
  private baseUrl: string = baseUrl+"/stat";

  public productsCount(modelId : number,startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/productsCount/"+modelId+"?beginDate=" + startDate + "&endDate=" + endDate);
  }
  /*public offersCount(startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/offersCount?beginDate=" + startDate + "&endDate=" + endDate);
  }*/

  public statAllModels(startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/statAllModels?beginDate=" + startDate + "&endDate=" + endDate);
  }

  public statStock(startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/statStock?beginDate=" + startDate + "&endDate=" + endDate);
  }

  public statAllPackets(startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/statAllPackets?beginDate=" + startDate + "&endDate=" + endDate);
  }
  public statAllColors(startDate: String,endDate:String,modelsListIdsArray:number[]) : Observable<any>{
    return this.http.get(this.baseUrl + "/statAllColors?beginDate=" + startDate + "&endDate=" + endDate + "&modelIds="+ modelsListIdsArray);
  }
  public statAllOffers(startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/statAllOffers?beginDate=" + startDate + "&endDate=" + endDate);
  }

  public statModelSold(modelId : number, startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/statModelSold/"+modelId+"?beginDate=" + startDate + "&endDate=" + endDate);
  }

  getStatsTreeNodesData(data: Packet[]) {

    interface CountCity { [cityName: string]: { count: number,confirm: number,citys: { [name: string]: { count: number,confirm: number} } }}
    interface CountPage { [pageName: string]: { count: number,confirm: number }}
    interface CountDate { [date: string]: { count: number,payed: number, return: number, exchange: number, out: number }}
    interface Count {cityCounts: CountCity,pageCounts:CountPage,dateCounts:CountDate}

    let cityCounts: CountCity = {};
    let pageCounts: CountPage = {};
    let dateCounts: CountDate = {};
        data.forEach((packet) => {
      //console.log('packet',packet);

      //count pages
      if (
        packet.fbPage != undefined
      ){
        if (pageCounts[packet.fbPage?.name]) {
          pageCounts[packet.fbPage?.name].count++;
        }
        else {
          pageCounts[packet.fbPage?.name] ={ count: 1, confirm: 0 };
        }
      }

      if (
        packet.date != undefined
      ){
        let date = packet.date.substring(0,10)
        if (dateCounts[date]) {
          dateCounts[date].count++;
        }
        else {
          dateCounts[date] ={ count: 1, payed: 0, return: 0, exchange: 0, out:0 };
        }
        if (packet.status == PAYEE || packet.status == LIVREE) {
          dateCounts[date].payed++;
        }
        else if (packet.status == RETOUR || packet.status == RETOUR_RECU) {
          dateCounts[date].return++;
        }

        if (packet.status == CONFIRMEE || packet.status == PAYEE || packet.status == LIVREE
          || packet.status == RETOUR || packet.status == RETOUR_RECU || packet.status == A_VERIFIER
          || packet.status == EN_COURS_1 || packet.status == EN_COURS_2 || packet.status == EN_COURS_3 || packet.status == EN_COURS
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
            cityCounts[packet.city?.governorate.name].citys[packet.city.name] ={ count: 1, confirm: 0 };
          }
        } else {
          cityCounts[packet.city?.governorate.name] = { count: 1, confirm: 0 ,citys:{}};
          cityCounts[packet.city?.governorate.name].citys[packet.city.name] = { count: 1, confirm: 0 };
        }
        if (packet.status == PAYEE || packet.status == LIVREE) {
          cityCounts[packet.city?.governorate.name].confirm++;
          cityCounts[packet.city?.governorate.name].citys[packet.city.name].confirm++;
        }
      }
    });
    let count : Count= { cityCounts, pageCounts, dateCounts}
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

