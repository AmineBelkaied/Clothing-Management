import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { FbPage } from 'src/shared/models/FbPage';
import { environment } from '../../environments/environment';
import { FB_PAGE_ENDPOINTS } from '../constants/api-endpoints';

@Injectable({
  providedIn: 'root'
})
export class FbPageService {

  private baseUrl: string = environment.baseUrl + `${FB_PAGE_ENDPOINTS.BASE}`;
  public fbPageSubscriber: BehaviorSubject<any> = new BehaviorSubject([]);
  public fbPage: BehaviorSubject<any> = new BehaviorSubject([]);
  public fbPages: FbPage[] = [];
  public editMode = false;
  constructor(private http: HttpClient) {

    /*this.findAllFbPages()
    .subscribe((fbPagesList: any) => {
        this.fbPageSubscriber.next(fbPagesList);
        //this.fbPages = fbPagesList.filter((fbPage: any) => fbPage.enabled);
    });*/
  }

  findAllFbPages() : Observable<any> {
    return this.http.get(`${this.baseUrl}`);
  }

  findFbPageById(id: number) {
    return this.http.get(`${this.baseUrl}/${id}`);
  }

  addFbPage(fbPage: FbPage) {
    return this.http.post(`${this.baseUrl}`, fbPage , {observe: 'body'});;
  }

  updateFbPage(fbPage: FbPage) {
    return this.http.put(`${this.baseUrl}`, fbPage , {headers : { 'content-type': 'application/json'}});
  }

  deleteFbPageById(id: any) {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  pushFbPage(fbPage: FbPage){
    this.fbPages.push(fbPage);
  }

  spliceFbPage(updatedFbPage: any){
    let index = this.fbPages.findIndex(fbPage => fbPage.id == updatedFbPage.id);
    console.log(index);
    this.fbPages.splice(index , 1 , updatedFbPage);
  }

  editFbPage(fbPage: FbPage) {
    this.fbPage.next(fbPage);
  }
}
