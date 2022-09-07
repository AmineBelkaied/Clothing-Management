import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Color } from '../models/color';

@Injectable({
  providedIn: 'root'
})
export class ColorService {

  private baseUrl: string = "http://localhost:2233/color";

  constructor(private http: HttpClient) { }

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
}
