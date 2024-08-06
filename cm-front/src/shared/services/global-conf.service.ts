import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { BehaviorSubject, catchError, Observable, shareReplay,tap,map, throwError, switchMap } from 'rxjs';
import { GlobalConf } from '../models/GlobalConf';
import { MessageService } from 'primeng/api';
import { GLOBAL_CONFIG_ENDPOINTS } from '../constants/api-endpoints';
import { OfferService } from './offer.service';

@Injectable({
  providedIn: 'root'
})
export class GlobalConfService {

  private baseUrl: string = environment.baseUrl + `${GLOBAL_CONFIG_ENDPOINTS.BASE}`;
  public editMode = false;
  public globalConfSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public globalConf: GlobalConf;

  constructor(private http: HttpClient, private messageService: MessageService,private offerService : OfferService) {}


  loadGlobalConf() : void {
    this.getGlobalConf().pipe(
      tap((globalConf: GlobalConf) => {
        // Emit the new global configuration through the subscriber
        this.globalConfSubscriber.next(globalConf);
        // Update the local state
        this.globalConf = globalConf;
      }),
      catchError((error) => {
        // Handle the error here
        console.error('Error loading global configuration:', error);
        return throwError(() => error);
      })
    ).subscribe();
  }

  getGlobalConf():Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  updateGlobalConf(globalConf: GlobalConf): Observable<any> {
    return this.http.put(`${this.baseUrl}`, globalConf , {headers : {'content-type': 'application/json'}});
  }

  getGlobalConfSubscriber(): Observable<GlobalConf> {
    return this.globalConfSubscriber.asObservable();
  }

  setGlobalConfSubscriber(globalConf:GlobalConf){
    this.updateGlobalConf(globalConf)
      .pipe(
        catchError((err: any, caught: Observable<any>): any => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: "Erreur lors de le modification' " + err.error.message,
          });
        })
      )
      .subscribe((result: any) => {
        this.globalConf = globalConf;
        this.globalConfSubscriber.next(globalConf)
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La taille a été modifiée avec succés", life: 1000 });
      });
  }

}
