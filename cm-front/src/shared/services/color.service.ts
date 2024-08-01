import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, tap, throwError } from 'rxjs';
import { Color } from '../models/Color';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class ColorService {

  private baseUrl: string = baseUrl+"/color";
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
    return this.http.get<Color[]>(this.baseUrl + "/findAll");
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

  spliceColor(updatedColor: any){
    let index = this.colors.findIndex(color => color.id == updatedColor.id);
    console.log(index);
    this.colors.splice(index , 1 , updatedColor);
  }

  editColor(color: Color) {
    this.color.next(color);
  }
}
