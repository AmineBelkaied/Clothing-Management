import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { DeliveryCompanyService } from 'src/shared/services/delivery-company.service';


@Component({
  selector: 'app-list-ste-Livraison',
  templateUrl: './list-ste-livraison.component.html',
  styleUrls: ['./list-ste-livraison.component.scss']
})
export class ListSteLivraisonComponent implements OnInit,OnDestroy {
  deliveryCompanyList: DeliveryCompany[] = [];
  $unsubscribe: Subject<void> = new Subject();

  constructor(private deliveryCompanyService: DeliveryCompanyService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService) {
                this.deliveryCompanyService.loadDeliveryCompanies();
              }

  ngOnInit(): void {
    this.deliveryCompanyService.getDCSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (deliveryCompany: DeliveryCompany[]) => {
        this.deliveryCompanyList = deliveryCompany;
      }
    );
  }

  editDeliveryCompany(deliveryCompany: any){
    this.deliveryCompanyService.editDeliveryCompany({...deliveryCompany});
    this.deliveryCompanyService.editMode = true;
  }

  deleteDeliveryCompany(deliveryCompany: any)  {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer la societe de livraison séléctionnée ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.deliveryCompanyService.deleteDeliveryCompanyById(deliveryCompany.id)
          .subscribe(result => {
            this.deliveryCompanyList = this.deliveryCompanyList.filter(val => val.id !== deliveryCompany.id);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La société de livraison a été supprimée avec succés", life: 1000 });
          })
      }
    });
  }

  enableDeliveryCompany(deliveryCompany: any)  {
    this.deliveryCompanyService.updateDeliveryCompany(deliveryCompany)
    .subscribe((updatedDeliveryCompany: any) => {
      this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La société de livraison a été modifiée avec succés", life: 1000 });
    });
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }



}
