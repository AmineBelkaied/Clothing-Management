import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';


@Component({
  selector: 'app-list-fbpages',
  templateUrl: './list-fbpages.component.html',
  styleUrls: ['./list-fbpages.component.scss']
})
export class ListFbpagesComponent implements OnInit,OnDestroy {

  fbPages: FbPage[] = [];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private fbPageService: FbPageService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {

     }

  ngOnInit(): void {
    //this.fbPages = this.fbPageService.fbPages;
    this.fbPageService.getFbPagesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (fbPages: FbPage[]) => {
        this.fbPages = fbPages;
      }
    );
  }


  editFbPage(fbPage: any){
    this.fbPageService.editFbPage({...fbPage});
    this.fbPageService.editMode = true;
  }

  deleteFbPage(fbPage: FbPage)  {
    this.fbPageService.checkFbPageUsage(fbPage.id!)
    .subscribe((fbPageUsage: any) => {
      if(fbPageUsage > 0) {
        this.messageService.add({
          severity: 'warn',
          summary: 'Attention !',
          detail:`La page facebook ne peut pas être supprimée car elle est utilisée au niveau des commandes ${fbPageUsage} fois` ,
          life: 5000,
        });
      } else {
        this.confirmationService.confirm({
          message: 'Êtes-vous sûr de vouloir supprimer la page facebook séléctionnée ?',
          header: 'Confirmation',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.fbPageService.deleteFbPageById(fbPage.id).pipe(takeUntil(this.$unsubscribe))
              .subscribe(() => {
                this.fbPages = this.fbPages.filter(val => val.id !== fbPage.id);
                this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été supprimée avec succés", life: 1000 });
              })
          }
        });
      }
    });
  }

  enableFbPage(fbPage: any)  {
    this.fbPageService.spliceFbPage(fbPage);
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
