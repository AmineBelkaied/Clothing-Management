import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Model } from 'src/shared/models/Model';
import { OfferModelsDTO } from 'src/shared/models/OfferModelsDTO';
import { Offer } from 'src/shared/models/Offer';
import { ModelService } from '../../../shared/services/model.service';
import { OfferService } from '../../../shared/services/offer.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-list-offers',
  templateUrl: './list-offers.component.html',
  styleUrls: ['./list-offers.component.css']
})
export class ListOffersComponent implements OnInit {


  offerDialog!: boolean;

  offers: Offer[] = [];

  models: Model[] = [];

  fbPages: FbPage[];

  editMode = false;

  selectedOffers: any[] = [];

  submitted: boolean = false;

  statuses: any[] = [];

  $unsubscribe: Subject<void> = new Subject();

  trackByFunction = (index: any, item: { id: any }) => {
    return item.id;
  };

  constructor(
    private offerService: OfferService,
    private modelService: ModelService,
    private fbPageService: FbPageService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  ngOnInit() {
    console.log("init list-offers");
    this.offerService.getOffersSubscriber()
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (offerList: any) => {
          if (offerList.length > 0)
            this.offers = offerList;
        },
        error: (err: any) => {
          console.error('Error fetching offers:', err);
        }
      });

    this.modelService.loadModels().pipe(takeUntil(this.$unsubscribe)).subscribe((modelList: any) => {
      this.models = modelList;
    });
  }

  openNew() {
    this.submitted = false;
    this.offerDialog = true;
    this.editMode = false;
  }

  deleteSelectedOffers() {
    let selectedOffersId = this.selectedOffers.map((selectedOffer: any) => selectedOffer.id);
    console.log(selectedOffersId);
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les offres séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.offerService.deleteSelectedOffers(selectedOffersId).pipe(takeUntil(this.$unsubscribe))
          .subscribe(result => {
            console.log("offers successfully deleted !");
            this.offers = this.offers.filter((offer: Offer) => selectedOffersId.indexOf(offer.id) == -1);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les offres séléctionnés ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  editOffer(offer: any) {
    this.offerService.setOffer(offer);
      this.offerDialog = true;
      this.editMode = true;
  }

  deleteOffer(offer: Offer) {
    console.log(offer.id)
    this.confirmationService.confirm({
      message: "Êtes-vous sûr de vouloir supprimer l'offre " + offer.name + "?",
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        /*-----------------------------------correction pour la suppression ------------------------------
        this.offerService.deleteOfferById(offer.id).pipe(takeUntil(this.$unsubscribe)).subscribe((response: any) => {
          this.offers = this.offers.filter(val => val.id !== offer.id);
          //this.offer = Object.assign({}, this.offer);
          this.messageService.add({ severity: 'success', summary: 'Succés', detail: "L'offre a été supprimée avec succés", life: 1000 });
        })
        */
      }
    });
  }


  findIndexById(id: number): number {
    let index = -1;
    for (let i = 0; i < this.offers.length; i++) {
      if (this.offers[i].id == id) {
        index = i;
        break;
      }
    }
    return index;
  }

  displayOfferModels(modelQuantities: OfferModelsDTO[]) {
//console.log("modelQuantities",modelQuantities);
    let offerModels = "";
    modelQuantities.forEach((modelQuantity, index) => {
      if (index < modelQuantities.length - 1 && index>0)
        offerModels += " , ";
      offerModels += modelQuantity.quantity + " " + modelQuantity.model.name;
    });
    return offerModels;
  }

  displayPages(fbPages: FbPage[]) {

    if(fbPages.length<1)return "null";
    let fbPgaesList = "";
    fbPages.forEach((fbPage, index) => {
      if (index < fbPgaesList.length - 1 && index>0)
        fbPgaesList += " , ";
      fbPgaesList += fbPage.name + " ";
    });
    return fbPgaesList;
  }

  hideDialog() {
    this.offerDialog = false;
    this.submitted = false;
  }

  OnSubmit($event: any) {
    let offerResponse = $event;
    if (this.editMode) {
      this.offers[this.findIndexById(offerResponse.offerId)] = offerResponse;
      this.messageService.add({ severity: 'success', summary: 'Successful', detail: "L'offre a été modifié avec succés", life: 1000 });
    } else {
      this.offers.push(offerResponse);
      this.messageService.add({ severity: 'success', summary: 'Successful', detail: "L'offre a été crée avec succés", life: 1000 });
    }
    this.hideDialog();
  }

  ngOnDestroy(): void {
    console.log("destroy list-offers");

    this.$unsubscribe.next();
  }
}
