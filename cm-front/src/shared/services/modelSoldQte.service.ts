import { Injectable } from '@angular/core';
import { Packet } from '../models/Packet';
import { PacketService } from './packet.service';
interface ModelSoldQteInterface {
  name?: string;
  occ: number;
}
@Injectable()
export class ModelSoldQte {
  //cityTree? : {name:string,occ:number}[];
/*   getSoldPacketData(data: Packet[]) {
    interface CountObject {
      [day: string]: {
        model:{
          [modelName: String]: {
            modelColors:{
              productRef: String;
              qte: number;
              colorName: String;
              sizes: { [sizeName: String]: {qte: number };
            };
          };
        };
      };
    }
  } */

    let counts: CountObject = {};

    data.forEach((packet) => {
      if (
        packet.city != undefined &&
        packet.city.name != undefined &&
        packet.city.governorate?.name != undefined
      ) {
        if (counts[packet.city?.governorate.name]) {
          counts[packet.city?.governorate.name].count++;
          if (counts[packet.city?.governorate.name].citys[packet.city.name])
            counts[packet.city?.governorate.name].citys[packet.city.name]
              .count++;
          else {
            counts[packet.city?.governorate.name].citys[packet.city.name] = {
              count: 1,
              confirm: 0,
            };
          }
        } else {
          counts[packet.city?.governorate.name] = {
            count: 1,
            confirm: 0,
            citys: {},
          };
          counts[packet.city?.governorate.name].citys[packet.city.name] = {
            count: 1,
            confirm: 0,
          };
        }
        if (packet.status == 'Payée') {
          counts[packet.city?.governorate.name].confirm++;
          counts[packet.city?.governorate.name].citys[packet.city.name]
            .confirm++;
        }
      }
    });
    return counts;
  }
}
