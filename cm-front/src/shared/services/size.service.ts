import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Size } from 'src/shared/models/Size';
import { baseUrl } from '../../assets/constants';


@Injectable({
  providedIn: 'root'
})
export class SizeService {

  private baseUrl: string = baseUrl+"/size";
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
    return this.http.get(this.baseUrl + "/findAll");
  }

  findSizeById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addSize(size: Size) {
    return this.http.post(this.baseUrl + "/add" , size , {observe: 'body'})
  }

  updateSize(size: Size) {
    return this.http.put(this.baseUrl + "/update" , size , {headers : { 'content-type': 'application/json'}})
  }

  deleteSizeById(idSize: any) {
    return this.http.delete(this.baseUrl + "/deleteById/" + idSize)
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
