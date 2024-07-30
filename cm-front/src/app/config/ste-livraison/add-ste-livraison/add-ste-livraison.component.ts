import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { SteLivraisonService } from 'src/shared/services/ste-livraison.service';


@Component({
  selector: 'app-add-ste-livraison',
  templateUrl: './add-ste-livraison.component.html',
  styleUrls: ['./add-ste-livraison.component.scss']
})
export class AddSteLivraisonComponent implements OnInit,OnDestroy {

  deliveryCompany!: DeliveryCompany;
  editMode!: boolean;
  $unsubscribe: Subject<void> = new Subject();
  constructor(public steLivraisonService: SteLivraisonService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.steLivraisonService.deliveryCompany.pipe(takeUntil(this.$unsubscribe)).subscribe(deliveryCompany => {
      this.deliveryCompany = deliveryCompany
    });
  }

  addDeliveryCompany(form: NgForm) {
    if(this.steLivraisonService.editMode){
      this.deliveryCompany.name = form.value.name;
      this.deliveryCompany.token = form.value.token;
      this.deliveryCompany.barreCodeUrl = form.value.barreCodeUrl;
      this.deliveryCompany.apiName = form.value.apiName;
      this.deliveryCompany.additionalName = form.value.additionalName;
      this.steLivraisonService.updateSte(this.deliveryCompany).pipe(takeUntil(this.$unsubscribe))
      .subscribe((updatedDC: any) => {
        console.log(updatedDC)
        this.steLivraisonService.spliceSte(updatedDC);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
        form.reset();
        this.steLivraisonService.editMode = false;
      });
    } else {
      this.steLivraisonService.addSte(form.value).pipe(takeUntil(this.$unsubscribe))
      .subscribe((addedDC: any) => {
        this.steLivraisonService.deliveryCompanyList.push(addedDC);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(ngForm: NgForm){
    ngForm.reset();
    this.steLivraisonService.editMode = false;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
this.$unsubscribe.complete();
  }

}
