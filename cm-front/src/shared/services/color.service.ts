import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { Color } from '../models/Color';
import { environment } from '../../environments/environment';
import { COLOR_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class ColorService {

  private baseUrl: string = environment.baseUrl + `${COLOR_ENDPOINTS.BASE}`;
  public colorsSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public color: BehaviorSubject<any> = new BehaviorSubject([]);
  public colors: Color[] = [];
  public editMode = false;
  
  constructor(private http: HttpClient) {
    this.findAllColors()
    .subscribe((colorList: any) => {
        this.colorsSubscriber.next(colorList);
        this.colors = colorList;
    });
  }

  findAllColors() {
    return this.http.get(`${this.baseUrl}`);
  }

  findColorById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addColor(color: Color) {
    return this.http.post(`${this.baseUrl}`, color , {observe: 'body'});
  }

  updateColor(color: Color) {
    return this.http.put(`${this.baseUrl}`, color , {headers : { 'content-type': 'application/json'}});
  }

  deleteColorById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  pushColor(color: Color){
    this.colors.push(color);
  }

  spliceColor(updatedColor: any){
    let index = this.colors.findIndex(color => color.id == updatedColor.id);
    console.log(index);
    this.colors.splice(index , 1 , updatedColor);
  }

  editColor(color: Color) {
    this.color.next(color);
  }
}
