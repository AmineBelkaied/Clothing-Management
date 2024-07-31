import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Size } from 'src/shared/models/Size';
import { environment } from '../../environments/environment';
import { SIZE_ENDPOINTS } from '../constants/api-endpoints';


@Injectable({
  providedIn: 'root'
})
export class SizeService {

  private baseUrl: string = environment.baseUrl + `${SIZE_ENDPOINTS.BASE}`;
  public sizesSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public size: BehaviorSubject<any> = new BehaviorSubject([]);
  public sizes: Size[] = [];
  public editMode = false;
  
  constructor(private http: HttpClient) {
    this.findAllSizes()
    .subscribe((sizeList: any) => {
        this.sizesSubscriber.next(sizeList);
        this.sizes = sizeList;
    });
  }

  findAllSizes() {
    return this.http.get(`${this.baseUrl}`);
  }

  findSizeById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addSize(size: Size) {
    return this.http.post(`${this.baseUrl}`, size , {observe: 'body'});
  }

  updateSize(size: Size) {
    return this.http.put(`${this.baseUrl}`, size , {headers : { 'content-type': 'application/json'}});
  }

  deleteSizeById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  pushSize(size: Size){
    this.sizes.push(size);
  }

  spliceSize(updatedSize: any){
    let index = this.sizes.findIndex(size => size.id == updatedSize.id);
    console.log(index);
    this.sizes.splice(index , 1 , updatedSize);
  }

  editSize(size: Size) {
    this.size.next(size);
  }
}
