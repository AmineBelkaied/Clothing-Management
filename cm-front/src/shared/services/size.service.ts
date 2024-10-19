import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, shareReplay, take, tap } from 'rxjs/operators';
import { Size } from 'src/shared/models/Size';
import { environment } from '../../environments/environment';
import { SIZE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class SizeService {

  private baseUrl: string = environment.baseUrl + `${SIZE_ENDPOINTS.BASE}`;
  public sizesSubscriber: BehaviorSubject<Size[]> = new BehaviorSubject<Size[]>([]);
  public size: BehaviorSubject<any> = new BehaviorSubject([]);
  public sizes: Size[] = [];
  public editMode = false;
  private sizesCache$: Observable<Size[]> | undefined;

  constructor(private http: HttpClient) {
  }

  loadSizes(): void {
    if (!this.sizesCache$) {
      this.sizesCache$ = this.findAllSizes().pipe(
        take(1),
        tap((sizeList: Size[]) => {
          this.sizes = sizeList;
          this.sizesSubscriber.next(this.sizes);
          console.log("findAllSizes");
        }),
        catchError((error) => {
          // Handle the error here
          console.error('Error fetching sizes', error);
          return throwError(() => error);
        }),
        shareReplay(1)
      );
    }
    this.sizesCache$.subscribe();
  }

  getSizesSubscriber(): Observable<Size[]> {
    if (this.sizesSubscriber.value.length === 0) {
      this.loadSizes();
    }
    return this.sizesSubscriber.asObservable();
  }



  getSizesByIds(ids: number[]) : Size[]{
    let sizes : Size[] = this.sizes.filter(size => ids.includes(size.id!));
    return sizes;
  }
  getSizesById(id: number) : Size | undefined{
    return this.sizes.find(size => id == size.id!);
  }

  getSizeNameById(id: number) : String{
    let sizeIndex = this.sizes.findIndex(size => size.id == id);
    return this.sizes[sizeIndex] != null ? this.sizes[sizeIndex].reference : "";
  }

  findAllSizes(): Observable<Size[]> {
    return this.http.get<Size[]>(`${this.baseUrl}`);
  }

  addSize(size: Size): Observable<Size> {
    return this.http.post<Size>(`${this.baseUrl}`, size, { observe: 'body' }).pipe(
      catchError(this.handleError<Size>('addSize'))
    );
  }

  updateSize(size: Size): Observable<void> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<void>(`${this.baseUrl}`, size , {headers }).pipe(
      catchError(this.handleError<void>('updateSize'))
    );
  }

  deleteSizeById(id: number) {
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      catchError(this.handleError<void>('deleteSizeById'))
    );
  }

  pushSize(size: Size): void {
    this.sizes.push(size);
    this.sizesSubscriber.next(this.sizes);
  }

  spliceSize(updatedSize: Size): void {
    const index = this.sizes.findIndex(size => size.id === updatedSize.id);
    if (index !== -1) {
      this.sizes.splice(index, 1, updatedSize);
      this.sizesSubscriber.next(this.sizes);
    }
  }

  editSize(size: Size): void {
    this.size.next(size);
  }

  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {
      console.error(`${operation} failed: ${error.message}`);
      return of(result as T);
    };
  }

  reloadColors(): void {
    this.sizesCache$ = undefined;
    this.loadSizes();
  }
}
