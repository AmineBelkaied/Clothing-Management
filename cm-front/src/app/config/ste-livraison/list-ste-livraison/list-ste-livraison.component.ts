import { Component, OnDestroy, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Subject, takeUntil } from 'rxjs';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { SteLivraisonService } from 'src/shared/services/ste-livraison.service';


@Component({
  selector: 'app-list-ste-Livraison',
  templateUrl: './list-ste-livraison.component.html',
  styleUrls: ['./list-ste-livraison.component.scss']
})
export class ListSteLivraisonComponent implements OnInit,OnDestroy {

  deliveryCompanyList: DeliveryCompany[] = [];
  $unsubscribe: Subject<void> = new Subject();

  constructor(private steLivraisonService: SteLivraisonService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService) {
                this.steLivraisonService.loadDeliveryCompanies();
              }

  ngOnInit(): void {
    this.steLivraisonService.getDCSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (dc: DeliveryCompany[]) => {
        this.deliveryCompanyList = dc;
      }
    );
  }

  editSte(ste: any){
    this.steLivraisonService.editSte({...ste});
    this.steLivraisonService.editMode = true;
  }

  deleteSte(ste: any)  {
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer la societe de livraison séléctionnée ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.steLivraisonService.deleteSteById(ste.id)
          .subscribe(result => {
            this.deliveryCompanyList = this.deliveryCompanyList.filter(val => val.id !== ste.id);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La societe de livraison a été supprimée avec succés", life: 1000 });
          })
      }
    });
  }

  enableSte(ste: any)  {
    //console.log("ste: " + ste.enabled);

    this.steLivraisonService.updateSte(ste)
    .subscribe((updatedSte: any) => {
      console.log(updatedSte);
      this.messageService.add({ severity: 'success', summary: 'Succés', detail: "La societe de livraison a été modifiée avec succés", life: 1000 });
    });
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
this.$unsubscribe.complete();
  }



}
