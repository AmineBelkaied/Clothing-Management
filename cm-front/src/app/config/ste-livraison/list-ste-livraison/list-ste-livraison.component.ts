import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { DeliveryCompany } from 'src/shared/models/DeliveryCompany';
import { GlobalConfService } from 'src/shared/services/global-conf.service';
import { SteLivraisonService } from 'src/shared/services/ste-livraison.service';


@Component({
  selector: 'app-list-ste-Livraison',
  templateUrl: './list-ste-livraison.component.html',
  styleUrls: ['./list-ste-livraison.component.scss']
})
export class ListSteLivraisonComponent implements OnInit {


  deliveryCompanyList: DeliveryCompany[] = [];

  constructor(private globalConfService: GlobalConfService,
              private steLivraisonService: SteLivraisonService,
              private messageService: MessageService,
              private confirmationService: ConfirmationService) { }
  config: any = {};

  ngOnInit(): void {
    this.steLivraisonService.deliveryCompanySubscriber
    .subscribe((stesList: any) => {
      this.deliveryCompanyList = stesList;
      //console.log("this.stes",this.deliveryCompanyList);
    });
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



}
