import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Size } from 'src/shared/models/Size';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class SizeService {

  private baseUrl: string = baseUrl + "/size";
  public sizesSubscriber: BehaviorSubject<Size[]> = new BehaviorSubject<Size[]>([]);
  public size: BehaviorSubject<any> = new BehaviorSubject([]);
  public sizes: Size[] = [];
  public editMode = false;

  constructor(private http: HttpClient) {
    //this.loadSizes();
  }

  loadSizes(): Observable<Size[]> {
    return this.findAllSizes().pipe(
      tap((sizeList: Size[]) => {
        this.sizes = sizeList;
        this.sizesSubscriber.next(this.sizes);
      }),
      catchError((error) => {
        // Handle the error here
        console.error('Error fetching sizes', error);
        return throwError(() => error);
      })
    );
  }

  getSizesSubscriber(): Observable<Size[]> {
    if (this.sizesSubscriber.value.length === 0) {
      this.loadSizes().subscribe();
    }
    return this.sizesSubscriber.asObservable();
  }


  findAllSizes(): Observable<Size[]> {
    return this.http.get<Size[]>(this.baseUrl + "/findAll").pipe(
      catchError(this.handleError<Size[]>('findAllSizes', []))
    );
  }

  findSizeById(id: number): Observable<Size> {
    return this.http.get<Size>(this.baseUrl + "/findById/" + id).pipe(
      catchError(this.handleError<Size>('findSizeById'))
    );
  }

  addSize(size: Size): Observable<Size> {
    return this.http.post<Size>(this.baseUrl + "/add", size, { observe: 'body' }).pipe(
      catchError(this.handleError<Size>('addSize'))
    );
  }

  updateSize(size: Size): Observable<void> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.put<void>(this.baseUrl + "/update", size, { headers }).pipe(
      catchError(this.handleError<void>('updateSize'))
    );
  }

  deleteSizeById(idSize: number): Observable<void> {
    return this.http.delete<void>(this.baseUrl + "/deleteById/" + idSize).pipe(
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
}
