import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, take, tap, throwError } from 'rxjs';
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
    this.getColorsSubscriber().pipe(take(1));
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
  getColorNameById(id: number) : String{
    let colorIndex = this.colors.findIndex(color => color.id == id);
    return this.colors[colorIndex] != null ? this.colors[colorIndex].name : "";
  }

  getColorByIds(ids: number[]) : Color[]{
    console.log("looking for colors by id : ",ids);
    return this.colors.filter(color => ids.includes(color.id!));
  }

  getColorById(id: number): Color | undefined {
    // Using the find method to locate the first matching color by its id.
    let color : Color | undefined =this.colors.find(color => color.id === id);
    return color
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
