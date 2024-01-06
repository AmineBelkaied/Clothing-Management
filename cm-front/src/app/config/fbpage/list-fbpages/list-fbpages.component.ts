import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';


@Component({
  selector: 'app-list-fbpages',
  templateUrl: './list-fbpages.component.html',
  styleUrls: ['./list-fbpages.component.scss']
})
export class ListFbpagesComponent implements OnInit {

  fbPages: FbPage[] = [];

  constructor(private fbPageService: FbPageService,private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.fbPageService.fbPageSubscriber
    .subscribe((fbPageList: any) => {
      this.fbPages = fbPageList;
      console.log("this.fbPages",this.fbPages);

    });
  }

  editFbPage(fbPage: any){
    this.fbPageService.editFbPage({...fbPage});
    this.fbPageService.editMode = true;
  }

  deleteFbPage(fbPage: any)  {
    console.log("okkk");

    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer la page facebook séléctionnée ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.fbPageService.deleteFbPageById(fbPage.id)
          .subscribe(result => {
            this.fbPages = this.fbPages.filter(val => val.id !== fbPage.id);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page faecbook a été supprimée avec succés", life: 1000 });
          })
      }
    });
  }

  enableFbPage(fbPage: any)  {
    console.log("fbpage: " + fbPage.enabled);

    this.fbPageService.updateFbPage(fbPage)
    .subscribe((updatedFbPage: any) => {
      console.log(updatedFbPage);
      this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
    });
  }
}
