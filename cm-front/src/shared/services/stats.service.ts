import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Packet } from '../models/Packet';
import { BehaviorSubject, Observable, Subject, tap, throwError } from 'rxjs';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  constructor(private http: HttpClient) {
  }
  //cityTree? : {name:string,occ:number}[];
  private baseUrl: string = baseUrl+"/packet";

  public productsCount(modelId : number,startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/productsCount/"+modelId+"?beginDate=" + startDate + "&endDate=" + endDate);
  }

  getStatsTreeNodesData(data: Packet[]) {

    interface CountCity { [cityName: string]: { count: number,confirm: number,citys: { [name: string]: { count: number,confirm: number} } }}
    interface CountPage { [pageName: string]: { count: number,confirm: number }}
    interface CountDate { [date: string]: { count: number,payed: number, out: number }}
    interface Count {cityCounts: CountCity,pageCounts:CountPage,dateCounts:CountDate}

    let cityCounts: CountCity = {};
    let pageCounts: CountPage = {};
    let dateCounts: CountDate = {};

    data.forEach((packet) => {
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
          dateCounts[date] ={ count: 1, payed: 0, out:0 };
        }
        if (packet.status == 'Payée' || packet.status == 'Livrée') {
          dateCounts[date].payed++;
        }
        if (packet.status == 'Confirmée' || packet.status == 'Payée' || packet.status == 'Livrée' || packet.status == 'Retour' || packet.status == 'Retour Expediteur' || packet.status == 'Retour reçu' || packet.status == 'En cours (1)' ||
        packet.status == 'En cours (2)' || packet.status == 'En cours (3)' || packet.status == 'En cours') {
          //|| packet.status.substring(0,7) == 'En Cours'
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
        if (packet.status == 'Payée' || packet.status == 'Livrée') {
          cityCounts[packet.city?.governorate.name].confirm++;
          cityCounts[packet.city?.governorate.name].citys[packet.city.name].confirm++;
        }
      }
    });
    let count : Count= { cityCounts, pageCounts, dateCounts}
    return count;
  }

}
