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

  constructor(private http: HttpClient) {
    this.findAllPackets()
      .subscribe({
        next: (allPackets: any) => {
          this.allPackets = allPackets;
          this.allPacketsReadySubject.next(true);
        },
        error: (error: any) => {
          console.log('Error:', error);
        },
        complete: () => {
          console.log('Observable completed-- All Packets From Base --');
        },
      })
  }

  public updateStatus(extractedBarcodes: string[], type: string){
    let updateStock = {'barCodes': extractedBarcodes,'status':type}
    return this.http.post(this.baseUrl + "/updatePacketsByBarCode", updateStock);
  }

  // Call this method whenever you want to access the "cached" request
  public findAllPackets(): Observable<any> {
    // only create a new request if you don't already have one stored
    // save your request
    return this.http.get(this.baseUrl + '/findAll');
  }

  // Call this method whenever you want to access the "cached" request
  public findAllTodaysPackets(): Observable<any> {
    // return the saved request
    return this.http.get(this.baseUrl + '/findAllTodaysPackets');
  }

  findPacketById(id: number) {
    return this.http.get(this.baseUrl + '/findById/' + id);
  }

  findPacketRelatedProducts(id: number) {
    return this.http.get(this.baseUrl + '/findPacketRelatedProducts/' + id);
  }

  addPacket(packet: Packet) {
    console.log('packet front before submit', packet);
    return this.http.post(this.baseUrl + '/add', packet);
  }

  updatePacket(packet: Packet) {
    console.log('new packetbefore update', packet);
    return this.http.put(this.baseUrl + '/update', packet, {
      headers: { 'content-type': 'application/json' },
    });
  }

  patchPacket(idPacket: any, packet: any) {
    console.log('new packetbefore patch', packet);
    return this.http.patch(this.baseUrl + '/patch/' + idPacket, packet, {
      headers: { 'content-type': 'application/json' },
    });
  }

  addProductsToPacket(selectedProducts: any) {
    console.log('selectedProducts', selectedProducts);
    return this.http.post(this.baseUrl + '/addProducts', selectedProducts, {
      headers: { 'content-type': 'application/json' },
    });
  }

  deletePacketById(idPacket: any) {
    console.log(this.baseUrl + '/deleteById/' + idPacket);
    return this.http.delete(this.baseUrl + '/deleteById/' + idPacket);
  }

  deleteSelectedPackets(packetsId: any[]) {
    return this.http.delete(
      this.baseUrl + '/deleteSelectedPackets/' + packetsId
    );
  }

  duplicatePacket(idPacket: any) {
    return this.http.get(this.baseUrl + '/duplicatePacket/' + idPacket);
  }

  getPacketAllStatus(idPacket: any) {
    return this.http.get(this.baseUrl + '/findPacketStatus/' + idPacket);
  }

  getLastFirstStatus(packet: Packet) {
    console.log('packet', packet);
    //this.isLoading = true;
    if (packet.status != 'Payée' && packet.status != 'Retour reçu' && packet.status != 'Retour Expediteur'&& packet.status != 'Livrée')
      this.getLastStatus(packet, 'FIRST')
        .subscribe(
          {
            next: (response: any) => {
              console.log('response', response);
              let pos = this.allPackets.indexOf(packet);
              this.allPackets.splice(pos, 1, response);
              this.allPacketsReadySubject.next(true);
            },
            error: (e: any) => {
            }
          });
  }
  getLastStatus(packet: Packet, deliveryCompany?: string) {
    return this.http.post(
      this.baseUrl + '/getLastStatus?deliveryCompany=' + deliveryCompany,
      packet
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
