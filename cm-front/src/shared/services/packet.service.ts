import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { baseUrl } from '../../assets/constants';
import { CONFIRMED, VALIDATION } from '../utils/status-list';
import { PacketValidationDTO } from '../models/PacketValidationDTO';

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

  public syncAllPackets(tenantName: string) : Observable<any> {
    let path = '/syncAllPacketsStatus?tenantName=' + tenantName;
    //let path = '/syncRupture';

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
  public findValidationPackets(): Observable<PacketValidationDTO> {
    let path = '/findValidationPackets';
    return this.http.get<PacketValidationDTO>(this.baseUrl + path);
  }

  public createDashboard(): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/createDashboard';
    return this.http.get(this.baseUrl+path);
  }

  public syncNotification(startDate: String,endDate:String): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/syncNotification?startDate=' + startDate + "&endDate=" + endDate;
    return this.http.get(this.baseUrl+path);
  }


  public findAllPacketsByDate(startDate: String,endDate:String): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    let path = '/findAllPacketsByDate?startDate=' + startDate + "&endDate=" + endDate;
    return this.http.get(this.baseUrl+path);
  }

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
    return this.http.put(this.baseUrl + '/patch/' + idPacket, packet, {
      headers: { 'content-type': 'application/json' },
    });
  }

  addProductsToPacket(selectedProducts: any): Observable<any> {
    //console.log('selectedProducts', selectedProducts);
    return this.http.post(this.baseUrl + '/addProducts', selectedProducts, {
      headers: { 'content-type': 'application/json' }
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
    if(state == VALIDATION)
      state=CONFIRMED;
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

  getLastStatus(id: number) {
    return this.http.post(this.baseUrl + '/getLastStatus',id);
  }


  addAttempt(packetId: number,note: string) {
    //packet.note = note;
    return this.http.post(
      this.baseUrl + '/addAttempt/'+packetId,note
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
