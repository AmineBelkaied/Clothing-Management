import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable, of, shareReplay, take, tap, throwError } from 'rxjs';
import { FbPage } from 'src/shared/models/FbPage';
import { environment } from '../../environments/environment';
import { MessageService } from 'primeng/api';
import { FB_PAGE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class FbPageService {

  private baseUrl: string = environment.baseUrl + `${FB_PAGE_ENDPOINTS.BASE}`;
  public fbPagesSubscriber: BehaviorSubject<FbPage[]> = new BehaviorSubject<FbPage[]>([]);
  public fbPage: BehaviorSubject<any> = new BehaviorSubject([]);
  public fbPages: FbPage[] = [];
  public editMode = false;
  private fbPagesCache$: Observable<FbPage[]> | undefined;

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  loadFbPages(): void {
    if (!this.fbPagesCache$) {
      this.fbPagesCache$ = this.findAllFbPages().pipe(
        take(1), // Ensures that the HTTP call is made only once
        tap((fbPagesList: FbPage[]) => {
          this.fbPages = fbPagesList;
          this.fbPagesSubscriber.next(fbPagesList);
          console.log("findAllFbPages");
        }),
        catchError((error) => {
          console.error('Error fetching fbPages', error);
          return throwError(() => error);
        })
        ,shareReplay(1) // Cache the response and share it with future subscribers
      );
    }

    this.fbPagesCache$.subscribe();
  }

  getFbPagesSubscriber(): Observable<FbPage[]> {
    if (this.fbPagesSubscriber.value.length === 0) {
      this.loadFbPages();
    }
    return this.fbPagesSubscriber.asObservable();
  }

  getFbPagesByIds(ids: number[]) : FbPage[]{
    return this.fbPages.filter(fbPage => ids.includes(fbPage.id!));
  }
  getFbPageById(id: number) : FbPage | undefined{
    return this.fbPages.find(fbPage => id == fbPage.id!);
  }

  getFbPageNameById(id: number) : String{
    let fbPageIndex = this.fbPages.findIndex(fbPage => fbPage.id == id);
    return this.fbPages[fbPageIndex] != null ? this.fbPages[fbPageIndex].name : "";
  }

  findAllFbPages() : Observable<FbPage[]> {
    return this.http.get<FbPage[]>(`${this.baseUrl}`);
  }

  findFbPageById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  saveFbPage(fbPage: FbPage) {
    return this.http.post(`${this.baseUrl}`, fbPage , {headers : { 'content-type': 'application/json'}});
  }

  checkFbPageUsage(id: number) {
    return this.http.get(`${this.baseUrl}${FB_PAGE_ENDPOINTS.CHECK_FB_PAGE_USAGE}/${id}`);
  }
  
  deleteFbPageById(id: any) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  pushFbPage(fbPage: FbPage){
    this.fbPages.push(fbPage);
    this.fbPagesSubscriber.next(this.fbPages);
  }

  spliceFbPage(updatedFbPage: any){
    let index = this.fbPages.findIndex(fbPage => fbPage.id == updatedFbPage.id);
    if(index>-1)
      {
        this.fbPages.splice(index , 1 , updatedFbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
      }
    else {
      this.fbPages.push(updatedFbPage);
      this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été ajouté avec succés", life: 1000 });
    }
    this.fbPagesSubscriber.next(this.fbPages);
  }

  editFbPage(fbPage: FbPage) {
    this.fbPage.next(fbPage);
  }

  reloadFbPages(): void {
    this.fbPagesCache$ = undefined;
    this.loadFbPages();
  }
}
