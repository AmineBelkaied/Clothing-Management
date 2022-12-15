import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { delay, Observable, shareReplay, tap } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { baseUrl,clientApiCode,deliveryUrl } from './constants';

@Injectable({
  providedIn: 'root'
})
export class PacketService {

  private baseUrl: string = baseUrl+"/packet";
  private deliveryUrl: string = deliveryUrl;
  private clientApiCode: string = clientApiCode;
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

  setDeliveryItem(receivername?: string, date_enlevement?: string, date_livraison?: string, somwhere?: string, gouvernorat_livraison?: number, telephone_de_contact_livraison?: string, code_postal_livraison?: number, nombre_de_colis?: number, libelle?: string, valeur_marchandise?: number) {
    return this.http.get(this.deliveryUrl + `diggie/additem?format=json&api_key=`+ this.clientApiCode +`&destinataire=${receivername}&user_name=diggie&date_enlevement=${date_enlevement}&date_livraison=${date_livraison}&adresse_de_livraison=${somwhere}&gouvernorat_livraison=${gouvernorat_livraison}&telephone_de_contact_livraison=${telephone_de_contact_livraison}&code_postal_livraison=${code_postal_livraison}&nombre_de_colis=${nombre_de_colis}&libelle_de_marchandise=${libelle}&valeur_marchandise=${valeur_marchandise}`);
  //  return this.http.get(this.deliveryUrl + `&destinataire=${receivername}&user_name=houb&date_enlevement=${date_enlevement}&date_livraison=${date_livraison}&adresse_de_livraison=${somwhere}&gouvernorat_livraison=${gouvernorat_livraison}&telephone_de_contact_livraison=${telephone_de_contact_livraison}&code_postal_livraison=${code_postal_livraison}&nombre_de_colis=${nombre_de_colis}&libelle_de_marchandise=${libelle}&valeur_marchandise=${valeur_marchandise}`);
  }
  getTrackingInfo(trackingNumber: string) {
   // return this.http.get(`http://pro.tunisia-express.tn/api/example/tracking/barcode/${trackingNumber}/api_key/3a824154b16ed7dab899bf000b80eeee/format/json`);
    return this.http.get(this.deliveryUrl + `example/tracking/barcode/${trackingNumber}/api_key/` + this.clientApiCode +`/format/json`);
  }

}
