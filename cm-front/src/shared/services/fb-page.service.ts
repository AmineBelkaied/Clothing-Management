import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { FbPage } from 'src/shared/models/FbPage';
import { baseUrl } from '../../assets/constants';

@Injectable({
  providedIn: 'root'
})
export class FbPageService {

  private baseUrl: string = baseUrl+"/fbPage";
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
