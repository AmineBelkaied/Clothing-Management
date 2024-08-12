import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
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

  constructor(private http: HttpClient, private messageService: MessageService) {
  }

  loadFbPages() : void{
    this.findAllFbPages()
    .subscribe({
      next: (fbPagesList: FbPage[]) => {
        this.fbPages = fbPagesList;
        this.fbPagesSubscriber.next(fbPagesList);},
      error: (error) => {
        console.error('Error fetching fb pages', error);
      }
    });
  }
  getFbPagesSubscriber(): Observable<FbPage[]> {
    if (this.fbPagesSubscriber.value.length === 0) {
      this.loadFbPages();
    }
    return this.fbPagesSubscriber.asObservable();
  }

  findAllFbPages() : Observable<FbPage[]> {
    return this.http.get<FbPage[]>(`${this.baseUrl}`);
  }

  setFbPagesConfSubscriber(fbPage:FbPage){
    this.updateFbPage(fbPage)
      .pipe(
        catchError((err: any): any => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: "Erreur lors de le modification' " + err.error.message,
          });
        })
      )
      .subscribe(() => {
        this.pushFbPage(fbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page FB a été modifiée avec succés", life: 1000 });
      });
  }

  findFbPageById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addFbPage(fbPage: FbPage) {
    return this.http.post(`${this.baseUrl}`, fbPage , {observe: 'body'});
  }

  updateFbPage(fbPage: FbPage) {
    return this.http.put(`${this.baseUrl}`, fbPage , {headers : { 'content-type': 'application/json'}});
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
    this.fbPages.splice(index , 1 , updatedFbPage);
    this.fbPagesSubscriber.next(this.fbPages);
  }

  editFbPage(fbPage: FbPage) {
    this.fbPage.next(fbPage);
  }
}
