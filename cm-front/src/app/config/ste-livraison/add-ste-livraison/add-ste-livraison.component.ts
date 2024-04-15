import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { SteLivraisonService } from 'src/shared/services/ste-livraison.service';


@Component({
  selector: 'app-add-ste-livraison',
  templateUrl: './add-ste-livraison.component.html',
  styleUrls: ['./add-ste-livraison.component.scss']
})
export class AddSteLivraisonComponent implements OnInit {

  deliveryCompany!: DeliveryCompany;
  editMode!: boolean;
  constructor(public steLivraisonService: SteLivraisonService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.steLivraisonService.deliveryCompany.subscribe(deliveryCompany => {
      this.deliveryCompany = deliveryCompany
    });
  }

  addDeliveryCompany(form: NgForm) {
    if(this.steLivraisonService.editMode){
      this.deliveryCompany.name = form.value.name;
      this.deliveryCompany.token = form.value.token;
      this.deliveryCompany.barreCodeUrl = form.value.barreCodeUrl;
      this.deliveryCompany.apiName = form.value.apiName;
      this.steLivraisonService.updateSte(this.deliveryCompany)
      .subscribe((updatedFbPage: any) => {
        console.log(updatedFbPage)
        this.steLivraisonService.spliceSte(updatedFbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
        form.reset();
        this.steLivraisonService.editMode = false;
      });
    } else {
      this.steLivraisonService.addSte(form.value)
      .subscribe((addedFbPage: any) => {
        this.steLivraisonService.deliveryCompanyList.push(addedFbPage);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(ngForm: NgForm){
    ngForm.reset();
    this.steLivraisonService.editMode = false;
  }

}
