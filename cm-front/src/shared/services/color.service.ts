import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, shareReplay, take, tap, throwError } from 'rxjs';
import { Color } from '../models/Color';
import { environment } from '../../environments/environment';
import { COLOR_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class ColorService {

  private baseUrl: string = environment.baseUrl + `${COLOR_ENDPOINTS.BASE}`;
  public colorsSubscriber: BehaviorSubject<Color[]> = new BehaviorSubject<Color[]>([]);
  //public color: BehaviorSubject<any> = new BehaviorSubject([]);
  public color: BehaviorSubject<any> = new BehaviorSubject([]);
  public colors: Color[] = [];
  public editMode = false;
  private colorsCache$: Observable<Color[]> | undefined;


  constructor(private http: HttpClient) {
  }

  loadColors(): void {
    if (!this.colorsCache$) {
      this.colorsCache$ = this.findAllColors().pipe(
        take(1),
        tap((colorList: Color[]) =>
          {
            this.colorsSubscriber.next(colorList);
            this.colors = colorList;
            console.log("findAllColors");
          }
        ),
        catchError((error) => {
          // Handle the error here
          console.error('Error fetching colors', error);
          return throwError(() => error);
        }),shareReplay(1)
      )
    }
    this.colorsCache$.subscribe();
  }

  getColorsSubscriber(): Observable<Color[]> {
    if (this.colorsSubscriber.value.length === 0) {
      this.loadColors()
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

  findColorById(id: number): Observable<Color> {
    return this.http.get<Color>(`${this.baseUrl}/${id}`)
      .pipe(
        catchError((error) => {
          console.error(`Error fetching color with id ${id}`, error);
          return throwError(() => error);
        })
      );
  }


  addColor(color: Color): Observable<Color> {
    return this.http.post<Color>(`${this.baseUrl}`, color,  {observe: 'body'})
      .pipe(
        catchError((error) => {
          console.error('Error adding color', error);
          return throwError(() => error);
        })
      );
  }

  updateColor(color: Color): Observable<Color> {
    return this.http.put<Color>(`${this.baseUrl}`, color, { headers: { 'Content-Type': 'application/json' } })
      .pipe(
        catchError((error) => {
          console.error('Error updating color', error);
          return throwError(() => error);
        })
      );
  }

  checkColorUsage(id: number) {
    return this.http.get(`${this.baseUrl}${COLOR_ENDPOINTS.CHECK_COLOR_USAGE}/${id}`);
  }

  deleteColorById(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`)
      .pipe(
        catchError((error) => {
          console.error('Error deleting color', error);
          return throwError(() => error);
        })
      );
  }

  pushColor(color: Color){
    this.colors.push(color);
  }

  spliceColor(updatedColor: Color) {
    const index = this.colors.findIndex(color => color.id === updatedColor.id);
    if (index !== -1) {
      this.colors.splice(index, 1, updatedColor);
    } else {
      console.error('Color not found in the list.');
    }
  }

  editColor(color: Color) {
    this.color.next(color);
  }

  reloadColors(): void {
    this.colorsCache$ = undefined;
    this.loadColors();
  }
}
