import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Packet } from 'src/shared/models/Packet';

@Injectable({
  providedIn: 'root'
})
export class PacketService {

  private baseUrl: string = "http://localhost:2233/packet";

  constructor(private http: HttpClient) { }

  findAllPackets() {
    return this.http.get(this.baseUrl + "/findAll");
  }

  findPacketById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }
  
  findPacketRelatedProducts(id: number) {
    return this.http.get(this.baseUrl + "/findPacketRelatedProducts/" + id);
  }

  addPacket(packet: Packet) {
    return this.http.post(this.baseUrl + "/add" , packet , {observe: 'body'})
  }

  updatePacket(packet: Packet) {
    return this.http.put(this.baseUrl + "/update" , packet , {headers : { 'content-type': 'application/json'}})
  }

  patchPacket(idPacket: any , packet: any) {
    console.log(packet);
    return this.http.patch(this.baseUrl + "/patch/" + idPacket , packet , {headers : { 'content-type': 'application/json'}})
  }

  addProductsToPacket(selectedProducts: any) {
    console.log(selectedProducts);
    return this.http.post(this.baseUrl + "/addProducts" , selectedProducts , {headers : { 'content-type': 'application/json'}})
  }

  deletePacketById(idPacket: any) {
    console.log(this.baseUrl + "/deleteById/" + idPacket)
    return this.http.delete(this.baseUrl + "/deleteById/" + idPacket)
  }

  deleteSelectedPackets(packetsId: any[]) {
    return this.http.delete(this.baseUrl + "/deleteSelectedPackets/" + packetsId);
  }

}
