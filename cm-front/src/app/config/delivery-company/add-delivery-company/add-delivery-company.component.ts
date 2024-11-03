import { Component, OnDestroy, OnInit } from '@angular/core';
import { NgForm } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';

@Component({
  selector: 'app-add-delivery-company',
  templateUrl: './add-delivery-company.component.html',
  styleUrls: ['./add-delivery-company.component.scss']
})
export class AddDeliveryCompanyComponent implements OnInit, OnDestroy {

  deliveryCompany!: DeliveryCompany;
  editMode!: boolean;
  $unsubscribe: Subject<void> = new Subject();
  deliveryOptions = [
    { label: 'FIRST', value: 'FIRST' },
    { label: 'NAVEX', value: 'NAVEX' },
    { label: 'JAX', value: 'JAX' },
  ];
  constructor(public deliveryCompanyService: DeliveryCompanyService, private messageService: MessageService) { }

  ngOnInit(): void {
    this.deliveryCompanyService.deliveryCompany.pipe(takeUntil(this.$unsubscribe)).subscribe(deliveryCompany => {
      this.deliveryCompany = deliveryCompany
    });
  }

  addDeliveryCompany(form: NgForm) {
    if(this.deliveryCompanyService.editMode){
      this.deliveryCompany.name = form.value.name;
      this.deliveryCompany.token = form.value.token;
      this.deliveryCompany.barcodeUrl = form.value.barcodeUrl;
      this.deliveryCompany.apiName = form.value.apiName;
      this.deliveryCompany.additionalName = form.value.additionalName;
      this.deliveryCompanyService.updateDeliveryCompany(this.deliveryCompany).pipe(takeUntil(this.$unsubscribe))
      .subscribe((updatedDeliveryCompany: any) => {
        this.deliveryCompanyService.spliceDeliveryCompany(updatedDeliveryCompany);
        this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La page facebook a été modifiée avec succés", life: 1000 });
        form.reset();
        this.deliveryCompanyService.editMode = false;
      });
    } else {
      this.deliveryCompanyService.addDeliveryCompany(form.value).pipe(takeUntil(this.$unsubscribe))
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

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }

}
