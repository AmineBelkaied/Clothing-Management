import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Subject } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';


@Component({
  selector: 'app-list-delivery-companies',
  templateUrl: './list-delivery-companies.component.html',
  styleUrls: ['./list-delivery-companies.component.scss']
})
export class ListDeliveryCompaniesComponent implements OnInit, OnDestroy {
  deliveryCompanyList: DeliveryCompany[] = [];
  $unsubscribe: Subject<void> = new Subject();

  constructor(private deliveryCompanyService: DeliveryCompanyService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
    this.deliveryCompanyService.loadDeliveryCompanies();
  }

  ngOnInit(): void {
    this.deliveryCompanyList = this.deliveryCompanyService.deliveryCompanyList;
    /*     this.deliveryCompanyService.getDeliveryCompaniesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
          (deliveryCompany: DeliveryCompany[]) => {
            this.deliveryCompanyList = deliveryCompany;
          }
        ); */
  }

  editDeliveryCompany(deliveryCompany: any) {
    this.deliveryCompanyService.editDeliveryCompany({ ...deliveryCompany });
    this.deliveryCompanyService.editMode = true;
  }

  deleteDeliveryCompany(deliveryCompany: any) {
    this.deliveryCompanyService.checkDeliveryCompanyUsage(deliveryCompany.id)
      .subscribe((deliveryCompanyUsage: any) => {
        if (deliveryCompanyUsage > 0) {
          this.messageService.add({
            severity: 'warn',
            summary: 'Attention !',
            detail: `La société de livraison ne peut pas être supprimée car elle est utilisée au niveau des commandes ${deliveryCompanyUsage} fois`,
            life: 5000,
          });
        } else {
          this.confirmationService.confirm({
            message: 'Êtes-vous sûr de vouloir supprimer la societe de livraison séléctionnée ?',
            header: 'Confirmation',
            icon: 'pi pi-exclamation-triangle',
            accept: () => {
              this.deliveryCompanyService.deleteDeliveryCompanyById(deliveryCompany.id)
                .subscribe(() => {
                  this.deliveryCompanyList = this.deliveryCompanyList.filter(val => val.id !== deliveryCompany.id);
                  this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La société de livraison a été supprimée avec succés", life: 1000 });
                })
            }
          });      
        }
      });
  }

  /*enableDeliveryCompany(deliveryCompany: any)  {
    this.deliveryCompanyService.updateDeliveryCompany(deliveryCompany)
    .subscribe((updatedDeliveryCompany: any) => {
      this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La société de livraison a été modifiée avec succés", life: 1000 });
    });
  }*/

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }



}
