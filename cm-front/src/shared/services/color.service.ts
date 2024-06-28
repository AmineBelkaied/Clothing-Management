import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { Color } from '../models/Color';
import { environment } from '../../environments/environment';
import { COLOR_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class ColorService {

  private baseUrl: string = environment.baseUrl + `${COLOR_ENDPOINTS.BASE}`;
  public colorsSubscriber: BehaviorSubject<Color[]> = new BehaviorSubject<Color[]>([]);
  public color: BehaviorSubject<any> = new BehaviorSubject([]);
  public colors: Color[] = [];
  public editMode = false;

  constructor(private http: HttpClient) {

  }

  loadColors(): Observable<Color[]>{
    return this.findAllColors().pipe(
      tap((colorList: Color[]) =>
        {
          this.colorsSubscriber.next(colorList);
          this.colors = colorList;
        }
      ),
      catchError((error) => {
        // Handle the error here
        console.error('Error fetching colors', error);
        return throwError(() => error);
      }))
  }

  getColorsSubscriber(): Observable<Color[]> {
    if (this.colorsSubscriber.value.length === 0) {
      this.loadColors().subscribe();
    }
    return this.colorsSubscriber.asObservable();
  }

  findAllColors() : Observable<Color[]> {
    return this.http.get<Color[]>(`${this.baseUrl}`);
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
