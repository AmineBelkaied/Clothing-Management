import { Component, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';

@Component({
  selector: 'app-add-ste-livraison',
  templateUrl: './add-ste-livraison.component.html',
  styleUrls: ['./add-ste-livraison.component.scss']
})
export class AddSteLivraisonComponent implements OnInit {

  deliveryCompany!: DeliveryCompany;
  editMode!: boolean;
  constructor(public deliveryCompanyService: DeliveryCompanyService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.deliveryCompanyService.deliveryCompany.subscribe(deliveryCompany => {
      this.deliveryCompany = deliveryCompany
    });
  }

  addDeliveryCompany(form: NgForm) {
    if(this.deliveryCompanyService.editMode){
      this.deliveryCompany.name = form.value.name;
      this.deliveryCompany.token = form.value.token;
      this.deliveryCompany.barreCodeUrl = form.value.barreCodeUrl;
      this.deliveryCompany.apiName = form.value.apiName;
      this.deliveryCompany.additionalName = form.value.additionalName;
      this.deliveryCompanyService.updateDeliveryCompany(this.deliveryCompany)
      .subscribe((updatedDeliveryCompany: any) => {
        this.deliveryCompanyService.spliceDeliveryCompany(updatedDeliveryCompany);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
        form.reset();
        this.deliveryCompanyService.editMode = false;
      });
    } else {
      this.deliveryCompanyService.addDeliveryCompany(form.value)
      .subscribe((addedDC: any) => {
        this.deliveryCompanyService.deliveryCompanyList.push(addedDC);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été crée avec succés", life: 1000 });
        form.reset();
      });
    }
  }

  reset(ngForm: NgForm){
    ngForm.reset();
    this.deliveryCompanyService.editMode = false;
  }

}
