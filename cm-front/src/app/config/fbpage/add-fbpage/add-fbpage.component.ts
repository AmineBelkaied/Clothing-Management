import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';


@Component({
  selector: 'app-add-fbpage',
  templateUrl: './add-fbpage.component.html',
  styleUrls: ['./add-fbpage.component.scss']
})
export class AddFbpageComponent implements OnInit,OnDestroy {

  fbPage!: FbPage;
  editMode!: boolean;
  $unsubscribe: Subject<void> = new Subject();
  constructor(public fbPageService: FbPageService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.fbPageService.fbPage.subscribe(fbPage => {
      this.fbPage = fbPage
    });
  }

  saveFbPage(form: NgForm) {
    console.log(form);

    this.fbPage.name = form.value.name;
    this.fbPage.link = form.value.link;
    if(this.fbPageService.editMode){
      this.fbPage.enabled = form.value.enabled;
      this.fbPage.deleted = false;
      this.fbPageService.updateFbPage(this.fbPage).pipe(takeUntil(this.$unsubscribe))
      .subscribe((updatedFbPage: any) => {
        console.log(updatedFbPage)
        this.fbPageService.spliceFbPage(updatedFbPage);

        form.reset();
        this.fbPageService.editMode = false;
      });
    }
    else {
      this.fbPage.enabled = true;
      this.fbPage.deleted = false;
      this.fbPageService.saveFbPage(this.fbPage).pipe(takeUntil(this.$unsubscribe))
      .subscribe((newFbPage: any) => {
        this.fbPageService.fbPages.push(newFbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La couleur a été crée avec succés", life: 1000 });
        form.reset();
        this.fbPageService.editMode = false;
      });
    }
    console.log("this.fbPage",this.fbPage);
  }

  reset(fbForm: NgForm){
    fbForm.reset();
    this.fbPageService.editMode = false;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

}
