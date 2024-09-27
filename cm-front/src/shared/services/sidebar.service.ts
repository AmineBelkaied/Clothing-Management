import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { Size } from 'src/shared/models/Size';
import { environment } from '../../environments/environment';
import { SIZE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class SideBarService {

  public isExpanded: BehaviorSubject<any> = new BehaviorSubject([]);

  constructor() {
    this.isExpanded.next(false);
  }


  idExpandedSubscriber(): Observable<boolean> {
    return this.isExpanded.asObservable();
  }
}
