import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Subject, tap, throwError } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root',
})
export class PacketService {

  private baseUrl: string = baseUrl + '/packet';
  public packetsRequest?: Observable<any>;
  //public todaysPacketsRequest?: Observable<any>;
  public allPackets: Packet[] = [];
  public allPacketsReadySubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  allPacketsReady$ = this.allPacketsReadySubject.asObservable();

  constructor(private http: HttpClient) {}

  public updateStatus(extractedBarcodes: string[], type: string){
    let updateStock = {'barCodes': extractedBarcodes,'status':type}
    return this.http.post(this.baseUrl + "/updatePacketsByBarCode", updateStock);
  }

  public syncAllPacketsFirst() : Observable<any> {
    let path = '/syncAllPacketsStatus';
    return this.http.get(this.baseUrl + path);
  }
  // Call this method whenever you want to access the "cached" request
  public findAllPackets(params: any): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/findAllPaginatedPackets?page=' + params.page + "&size=" + params.size;
    if(params.searchText != undefined && params.searchText != null)
      path += "&searchText=" + params.searchText;
    if(params.startDate != undefined)
      path += "&startDate=" + params.startDate;
    if(params.startDate != undefined && params.endDate != undefined)
      path += "&endDate=" + params.endDate;
    if(params.status != undefined && params.status != null)
      path += "&status=" + params.status;
    if(params.mandatoryDate != undefined && params.mandatoryDate != null)
      path += "&mandatoryDate=" + params.mandatoryDate;
    return this.http.get(this.baseUrl + path);
  }

  public createDashboard(): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/createDashboard';
    return this.http.get(this.baseUrl+path);
  }

  public syncNotification(): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/syncNotification';
    return this.http.get(this.baseUrl+path);
  }


  public findAllPacketsByDate(startDate: String,endDate:String): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/findAllPacketsByDate?startDate=' + startDate + "&endDate=" + endDate;
    return this.http.get(this.baseUrl+path);
  }

 /* public findAllPacketsByDateAndModels(startDate: String,endDate:String,models:number[]): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/findAllPacketsByDateAndModels?startDate=' + startDate + "&endDate=" + endDate + "&models=" + models;
    return this.http.get(this.baseUrl+path);
  }*/

  // Call this method whenever you want to access the "cached" request
  /*public findAllTodaysPackets(): Observable<any> {
    // return the saved request
    return this.http.get(this.baseUrl + '/findAllTodaysPackets');
  }*/

  findPacketById(id: number): Observable<any> {
    return this.http.get(this.baseUrl + '/findById/' + id);
  }

  findPacketRelatedProducts(id: number): Observable<any> {
    return this.http.get(this.baseUrl + '/findPacketRelatedProducts/' + id);
  }

  addPacket(): Observable<any> {
    //console.log('packet front before submit', packet);
    return this.http.get(this.baseUrl + '/add');
  }

  updatePacket(packet: Packet): Observable<any> {
    //console.log('new packetbefore update', packet);
    return this.http.put(this.baseUrl + '/update', packet, {
      headers: { 'content-type': 'application/json' },
    });
  }

  patchPacket(idPacket: any, packet: any): Observable<any> {
    //console.log('new packetbefore patch', packet);
    return this.http.patch(this.baseUrl + '/patch/' + idPacket, packet, {
      headers: { 'content-type': 'application/json' },
    });
  }

  addProductsToPacket(selectedProducts: any,stock:number): Observable<any> {
    //console.log('selectedProducts', selectedProducts);
    return this.http.post(this.baseUrl + '/addProducts?stock='+stock, selectedProducts, {
      headers: { 'content-type': 'application/json' },
    });
  }

  deletePacketById(idPacket: any) {
    //console.log(this.baseUrl + '/deleteById/' + idPacket);
    return this.http.delete(this.baseUrl + '/deleteById/' + idPacket);
  }

  deleteSelectedPackets(packetsId: any[]) {
    return this.http.delete(
      this.baseUrl + '/deleteSelectedPackets/' + packetsId
    );
  }


  validatePacket(barCode: any,state: string): Observable<any> {
    let path ='/valid/' + barCode;
    return this.http.post(this.baseUrl + path, state, {
      headers: { 'content-type': 'application/json' },
    });
  }

  duplicatePacket(idPacket: any) {
    return this.http.get(this.baseUrl + '/duplicatePacket/' + idPacket);
  }

  getLastStock(packetId: number):Observable<any> {
    console.log("getLastStock"+packetId);
    return this.http.get(this.baseUrl +'/checkPacketProductsValidity/'+packetId);
  }

  getPacketTimeLine(idPacket: any) {
    return this.http.get(this.baseUrl + '/getPacketTimeLine/' + idPacket);
  }

  getLastStatus(packet: Packet) {
    return this.http.post(
      this.baseUrl + '/getLastStatus',packet
    );
  }


  addAttempt(packet: Packet,note: string) {
    //packet.note = note;
    return this.http.post(
      this.baseUrl + '/addAttempt/'+note,packet
    );
  }

  handleError(error: any) {
    let errorMessage = '';
    if (error.error instanceof ErrorEvent) {
      // client-side error
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // server-side error
      errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
    }
    console.log(errorMessage);
    return throwError(() => {
      return errorMessage;
    });
  }
}
