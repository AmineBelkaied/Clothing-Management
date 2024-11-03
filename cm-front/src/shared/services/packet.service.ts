import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, throwError } from 'rxjs';
import { Packet } from 'src/shared/models/Packet';
import { environment } from '../../environments/environment';
import { CONFIRMED, VALIDATION } from '../utils/status-list';
import { Note } from '../models/Note';
import { PACKET_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root',
})
export class PacketService {

  private baseUrl: string = environment.baseUrl + `${PACKET_ENDPOINTS.BASE}`;
  public packetsRequest?: Observable<any>;
  public allPackets: Packet[] = [];
  public allPacketsReadySubject: BehaviorSubject<any> = new BehaviorSubject<any>(null);
  allPacketsReady$ = this.allPacketsReadySubject.asObservable();

  constructor(private http: HttpClient) { }

  public updateStatus(extractedBarcodes: string[], type: string) {
    let updateStock = { 'barcodes': extractedBarcodes, 'status': type }
    return this.http.post(`${this.baseUrl}${PACKET_ENDPOINTS.BARCODE_STATUS}`, updateStock);
  }

  public syncAllPackets(tenantName: string): Observable<any> {
    return this.http.get(`${this.baseUrl}${PACKET_ENDPOINTS.STATUS_SYNC}?tenantName=${tenantName}`);
  }

  public findAllPackets(params: any): Observable<any> {
    let httpParams = new HttpParams()
      .set('page', params.page)
      .set('size', params.size);

    if (params.searchText) {
      httpParams = httpParams.set('searchText', params.searchText);
    }
    if (params.beginDate) {
      httpParams = httpParams.set('beginDate', params.beginDate);
    }
    if (params.beginDate && params.endDate) {
      httpParams = httpParams.set('endDate', params.endDate);
    }
    if (params.status) {
      httpParams = httpParams.set('status', params.status);
    }
    if (params.mandatoryDate) {
      httpParams = httpParams.set('mandatoryDate', params.mandatoryDate);
    }

    const path = `${this.baseUrl}${PACKET_ENDPOINTS.PAGINATED}`;
    return this.http.get(path, { params: httpParams });
  }

  public syncNotification(beginDate: string, endDate: string): Observable<any> {
    let params = new HttpParams()
      .set('beginDate', beginDate)
      .set('endDate', endDate);

    return this.http.get(`${this.baseUrl}${PACKET_ENDPOINTS.NOTIFICATIONS_SYNC}`, { params });
  }

  public findPacketById(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  public findPacketRelatedProducts(id: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${id}${PACKET_ENDPOINTS.RELATED_PRODUCTS}`);
  }

  public addPacket(): Observable<any> {
    return this.http.post(`${this.baseUrl}`, {});
  }

  public updatePacket(packet: Packet): Observable<any> {
    return this.http.put(`${this.baseUrl}`, packet, { headers: { 'content-type': 'application/json' } });
  }

  public patchPacket(id: number, packet: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, packet, {
      headers: { 'content-type': 'application/json' }
    });
  }

  public addProductsToPacket(selectedProducts: any): Observable<any> {
    return this.http.post(`${this.baseUrl}${PACKET_ENDPOINTS.ADD_PRODUCTS}`, selectedProducts, {
      headers: { 'content-type': 'application/json' }
    });
  }

  deletePacketById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  deleteSelectedPackets(packetsId: number[], note: Note) {
    const params = new HttpParams().set('packetsId', packetsId.join(','));
    return this.http.post(`${this.baseUrl}${PACKET_ENDPOINTS.BATCH_DELETE}`, note, {
      headers: { 'content-type': 'application/json' },
      params: params
    }).pipe(
      catchError((error) => {
        console.error('Error:', error);
        return throwError(error);
      })
    );
  }


  validatePacket(barcode: string, state: string): Observable<any> {
    if (state == VALIDATION)
      state = CONFIRMED;
    return this.http.post(`${this.baseUrl}${PACKET_ENDPOINTS.VALIDATE}/${barcode}`, state, {
      headers: { 'content-type': 'application/json' },
    });
  }

  duplicatePacket(id: number) {
    return this.http.get(`${this.baseUrl}${PACKET_ENDPOINTS.DUPLICATE}/${id}`);
  }

  /*getLastStock(packetId: number): Observable<any> {
    return this.http.get(`${this.baseUrl}/${packetId}${PACKET_ENDPOINTS.CHECK_VALIDITY}`);
  }*/

  getPacketTimeLine(packetId: number) {
    return this.http.get(`${this.baseUrl}/${packetId}${PACKET_ENDPOINTS.TIMELINE}`);
  }

  getLastStatus(id: number) {
    return this.http.post(
      `${this.baseUrl}${PACKET_ENDPOINTS.STATUS}`, id);
  }

  addAttempt(note: Note, packetId: number) {
    return this.http.post(
      `${this.baseUrl}/${packetId}${PACKET_ENDPOINTS.ATTEMPT}`, note
    );
  }
  getValidationPackets(): Observable<any> {
    return this.http.get(`${this.baseUrl}${PACKET_ENDPOINTS.VALIDATION}`);
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
