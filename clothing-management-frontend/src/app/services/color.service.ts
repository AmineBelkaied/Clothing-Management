import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Color } from '../models/color';

@Injectable({
  providedIn: 'root'
})
export class ColorService {

  private baseUrl: string = "http://localhost:2233/color";
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
    return this.http.get(this.baseUrl + "/findAll");
  }
  
  findColorById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addColor(color: Color) {
    return this.http.post(this.baseUrl + "/add" , color , {observe: 'body'})
  }

  updateColor(color: Color) {
    return this.http.put(this.baseUrl + "/update" , color , {headers : { 'content-type': 'application/json'}})
  }

  deleteColorById(idColor: any) {
    return this.http.delete(this.baseUrl + "/deleteById/" + idColor)
  }

  pushColor(color: Color){
    this.colors.push(color);
  }

  //
  spliceColor(color: any){
    
    console.log(color)
    let index = this.colors.findIndex(color => color);
    console.log(index);
    
    let updatedColor = Object.assign({} , color);  
    console.log(updatedColor);
    
    this.colors.splice(index , 1 , updatedColor);
  }
  
  editColor(color: Color) {
    this.color.next(color);
  }
}
