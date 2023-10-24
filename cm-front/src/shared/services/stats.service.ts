import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Packet } from '../models/Packet';
import { BehaviorSubject, Observable, Subject, tap, throwError } from 'rxjs';
import { baseUrl } from '../../assets/constants';
import { ProductCountDTO } from '../models/ProductCountDTO';
import { DateUtils } from '../utils/date-utils';
import { LogarithmicScale } from 'chart.js';

@Injectable({
  providedIn: 'root'
})
export class StatsService {
  constructor(private http: HttpClient,private dateUtils: DateUtils) {
  }
  //cityTree? : {name:string,occ:number}[];
  private baseUrl: string = baseUrl+"/packet";

  public productsCount(modelId : number,startDate: String,endDate:String) : Observable<any>{
    return this.http.get(this.baseUrl + "/productsCount/"+modelId+"?beginDate=" + startDate + "&endDate=" + endDate);
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
    console.log('packets',data);

    data.forEach((packet) => {
      //console.log('packet',packet);

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
        if (packet.status == 'Payée' || packet.status == 'Livrée') {
          dateCounts[date].payed++;
        }
        else if (packet.status == 'Retour' || packet.status == 'Retour reçu') {
          dateCounts[date].return++;
        }

        if (packet.status == 'Confirmée' || packet.status == 'Payée' || packet.status == 'Livrée' || packet.status == 'Retour' || packet.status == 'Retour reçu' || packet.status == 'En cours (1)' ||
        packet.status == 'En cours (2)' || packet.status == 'En cours (3)' || packet.status == 'En cours') {
          //|| packet.status.substring(0,7) == 'En Cours'
          if (packet.exchange) {
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
        if (packet.status == 'Payée' || packet.status == 'Livrée') {
          cityCounts[packet.city?.governorate.name].confirm++;
          cityCounts[packet.city?.governorate.name].citys[packet.city.name].confirm++;
        }
      }
    });
    let count : Count= { cityCounts, pageCounts, dateCounts}
    console.log(count);

    return count;
  }


  // Usage
/*   const dateToCheck = '2023-10-08';
  const redColorExists = doesRedColorExist(dateToCheck);

  if (redColorExists) {
    console.log("Red color exists for", dateToCheck);
  } else {
    console.log("Red color does not exist for", dateToCheck);
  } */



/*   getModelStatsTreeNodesData(data: ProductCountDTO[]) {


    interface CountModelDate { [date: string]: { products : [{name: string, count: number}], count: number}}
    //interface CountModelDate { [date: string]: { products : [{name: string, count: number}] , colors: [{name: string, count: number}],sizes: [{name: string, count: number}], count: number}}






    let modelDateCounts: CountModelDate = {};

    console.log('Stat-packets',data);
    const datesOut: Date[] = Object.values(data).flatMap(
      (obj) => obj.packetDate
    );
    datesOut.forEach((dateRow) => {
      let oneDay = data.filter((obj) => obj.packetDate =dateRow)
      oneDay.forEach((product) => {
        if (
          product.packetDate != undefined
        ){
          let date = product.packetDate+"";

          console.log("modelDateCountsBefore",modelDateCounts);
          console.log('date',date);

          if (modelDateCounts[date]) {
            console.log("oldDate",date);

            let pos=modelDateCounts[date].products.findIndex( x => x.name === product.productId+"");
            console.log("pos",pos);

            if (pos>-1) {
              console.log("oldProduct",product.productId);
              modelDateCounts[date].count+=product.count;
              modelDateCounts[date].products[pos].count += product.count;
            }
            else {
              modelDateCounts[date].products.push({ name : product.productId+"", count : product.count });
              console.log("newProduct",product.productId+" "+product.count);

            }
          }
          else {
            console.log("newDate",date+" pc "+product.count);

            modelDateCounts[date] ={
                                    products : [{ name : product.productId+"", count : product.count }],
                                    count : product.count };
            console.log("modelDateCountsafter",modelDateCounts);
          }
        }

      });

    });
    console.log('modelDateCounts',modelDateCounts);
  } */



  getSales() {
    return this.http.get<any>('assets/sales.json')
        .toPromise()
        .then(res => <DaySales[]>res.sales)
        .then(data => { return data; });
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

