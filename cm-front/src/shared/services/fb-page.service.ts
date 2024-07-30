import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, catchError, Observable } from 'rxjs';
import { FbPage } from 'src/shared/models/FbPage';
import { baseUrl } from '../../assets/constants';
import { MessageService } from 'primeng/api';

@Injectable({
  providedIn: 'root'
})
export class FbPageService {



  private baseUrl: string = baseUrl+"/fbPage";
  public fbPageSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public fbPage: BehaviorSubject<any> = new BehaviorSubject([]);
  public fbPages: FbPage[] = [];
  public editMode = false;

  constructor(private http: HttpClient, private messageService: MessageService) {

  }
  loadFbPages(){
    this.findAllFbPages()
    .subscribe((fbPagesList: any) => {
        this.fbPages = fbPagesList;
        this.fbPageSubscriber.next(fbPagesList);
    });
  }

  setFbPagesConfSubscriber(fbPage:FbPage){
    this.updateFbPage(fbPage)
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
        this.pushFbPage(fbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page FB a été modifiée avec succés", life: 1000 });
      });
  }

  getFbPagesSubscriber(): Observable<FbPage[]> {
    return this.fbPageSubscriber.asObservable();
  }
  findAllFbPages() : Observable<any> {
    return this.http.get(this.baseUrl + "/findAll");
  }

  findFbPageById(id: number) {
    return this.http.get(this.baseUrl + "/findById/" + id);
  }

  addFbPage(fbPage: FbPage) {
    return this.http.post(this.baseUrl + "/add" , fbPage , {observe: 'body'})
  }

  updateFbPage(fbPage: FbPage) {
    return this.http.put(this.baseUrl + "/update" , fbPage , {headers : { 'content-type': 'application/json'}})
  }

  deleteFbPageById(idFbPage: any) {
    return this.http.delete(this.baseUrl + "/deleteById/" + idFbPage)
  }

  pushFbPage(fbPage: FbPage){
    this.fbPages.push(fbPage);
    this.fbPageSubscriber.next(this.fbPages);
  }

  spliceFbPage(updatedFbPage: any){
    let index = this.fbPages.findIndex(fbPage => fbPage.id == updatedFbPage.id);
    this.fbPages.splice(index , 1 , updatedFbPage);
    this.fbPageSubscriber.next(this.fbPages);
  }

  editFbPage(fbPage: FbPage) {
    this.fbPage.next(fbPage);

  }
}
