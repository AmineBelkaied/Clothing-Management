import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';


@Component({
  selector: 'app-add-fbpage',
  templateUrl: './add-fbpage.component.html',
  styleUrls: ['./add-fbpage.component.scss']
})
export class AddFbpageComponent implements OnInit {

  fbPage!: FbPage;
  editMode!: boolean;
  constructor(public fbPageService: FbPageService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.fbPageService.fbPage.subscribe(fbPage => {
      this.fbPage = fbPage
    });
  }

  addFbPage(form: NgForm) {
    if(this.fbPageService.editMode){
      this.fbPage.name = form.value.name;
      this.fbPage.link = form.value.link;
      this.fbPageService.updateFbPage(this.fbPage)
      .subscribe((updatedFbPage: any) => {
        console.log(updatedFbPage) 
        this.fbPageService.spliceFbPage(updatedFbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
        form.reset();
        this.fbPageService.editMode = false;
      });
    } else {
      this.fbPageService.addFbPage(form.value)
      .subscribe((addedFbPage: any) => {
        this.fbPageService.fbPages.push(addedFbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(fbForm: NgForm){
    fbForm.reset();
    this.fbPageService.editMode = false;
  }

}
