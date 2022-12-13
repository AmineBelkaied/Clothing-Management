import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { delay, Observable, shareReplay, tap } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { baseUrl } from './constants';

@Injectable({
  providedIn: 'root'
})
export class PacketService {

  private baseUrl: string = baseUrl+"/packet";
  public packetsRequest?: Observable<any>;
  public todaysPacketsRequest?: Observable<any>;
  public allPackets: Packet[] = [];

  constructor(private http: HttpClient) {
    this.findAllPackets().pipe(
      tap(() => console.log("Waiting 5 seconds !"))
    ).subscribe((allPackets: any) => {
      this.allPackets = allPackets;
      console.log("-- All Packets --");
      console.log(this.allPackets);
    });
  }

  // Call this method whenever you want to access the "cached" request
  public findAllPackets(): Observable<any> {
    // only create a new request if you don't already have one stored
    if (!this.packetsRequest) {
      // save your request
      this.packetsRequest = this.http.get(this.baseUrl + "/findAll").pipe(
        // Share the result - else every .subscribe will create another request, which you don't want
        shareReplay(1)
      )
    }
    // return the saved request
    return this.packetsRequest;
  }

  // Call this method whenever you want to access the "cached" request
  public findAllTodaysPackets(): Observable<any> {
    // only create a new request if you don't already have one stored
    /*if (!this.todaysPacketsRequest) {
      // save your request
      this.todaysPacketsRequest = this.http.get(this.baseUrl + "/findAllTodaysPackets").pipe(
        // Share the result - else every .subscribe will create another request, which you don't want
        shareReplay(1)
      )
    }*/
    // return the saved request
    return this.http.get(this.baseUrl + "/findAllTodaysPackets");
  }

  findPacketById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  findPacketRelatedProducts(id: number) {
    return this.http.get(this.baseUrl + "/findPacketRelatedProducts/" + id);
  }

  addPacket(packet: Packet) {
    return this.http.post(this.baseUrl + "/add", packet)
  }

  updatePacket(packet: Packet) {
    console.log('new packetbefore update',packet);
    return this.http.put(this.baseUrl + "/update", packet, { headers: { 'content-type': 'application/json' } })
  }

  patchPacket(idPacket: any, packet: any) {
    console.log('new packetbefore patch',packet);
    return this.http.patch(this.baseUrl + "/patch/" + idPacket, packet, { headers: { 'content-type': 'application/json' } })
  }

  addProductsToPacket(selectedProducts: any) {
    console.log('selectedProducts',selectedProducts);
    return this.http.post(this.baseUrl + "/addProducts", selectedProducts, { headers: { 'content-type': 'application/json' } })
  }

  deletePacketById(idPacket: any) {
    console.log(this.baseUrl + "/deleteById/" + idPacket)
    return this.http.delete(this.baseUrl + "/deleteById/" + idPacket)
  }

  deleteSelectedPackets(packetsId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedPackets/" + packetsId);
  }

  getPacketDescription(packetDescription:any ,modelName: any, color: any, size: any) {
    if (modelName != null)
      packetDescription += modelName;
    if (color != null)
      packetDescription += " " + color;
    if (size != null)
      packetDescription += " (" + size + ")";
    packetDescription += " , ";
  }
}
