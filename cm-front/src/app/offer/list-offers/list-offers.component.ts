import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Model } from 'src/shared/models/Model';
import { ModelQuantity } from 'src/shared/models/ModelQuantity';
import { Offer } from 'src/shared/models/Offer';
import { OfferModelDTO } from 'src/shared/models/OfferModelDTO';
import { ModelService } from '../../../shared/services/model.service';
import { OfferService } from '../../../shared/services/offer.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';

@Component({
  selector: 'app-list-offers',
  templateUrl: './list-offers.component.html',
  styleUrls: ['./list-offers.component.css']
})
export class ListOffersComponent implements OnInit {


  offerDialog!: boolean;

  offers: OfferModelDTO[] = [];
  models: Model[] = [];
  fbPages: FbPage[];

  offer: OfferModelDTO = {
    "offerId": "",
    "name": "",
    "modelQuantities": [],
    "fbPages" : [],
    "price": 0,
    "enabled": false,
  }
  editMode = false;

  selectedOffers: Offer[] = [];

  submitted: boolean = false;

  statuses: any[] = [];

  constructor(private offerService: OfferService, private modelService: ModelService, private fbPageService: FbPageService, private messageService: MessageService,
    private confirmationService: ConfirmationService) {
  }

  ngOnInit() {
    this.offerService.findAllOffersModelQuantities().subscribe((offerList: any) => {
      this.offers = offerList;
      console.log(this.offers)
    });
    this.modelService.findAllModels().subscribe((modelList: any) => {
      this.models = modelList;
    });
    this.fbPageService.findAllFbPages().subscribe((fbPageList: any) => {
      this.fbPages = fbPageList;
    });
  }

  openNew() {
    this.offer = {
      "name": "",
      "modelQuantities": [],
      "fbPages" : [],
      "price": 0,
      "enabled": false,
    }
    this.submitted = false;
    this.offerDialog = true;
    this.editMode = false;
  }

  deleteSelectedOffers() {
    let selectedOffersId = this.selectedOffers.map((selectedOffer: any) => selectedOffer.offerId);
    console.log(selectedOffersId);
    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les offres séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.offerService.deleteSelectedOffers(selectedOffersId)
          .subscribe(result => {
            console.log("offers successfully deleted !");
            this.offers = this.offers.filter((offer: OfferModelDTO) => selectedOffersId.indexOf(offer.offerId) == -1);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les offres séléctionnés ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  editOffer(offer: OfferModelDTO) {
    this.offer = { ...offer };
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
        this.offerService.deleteOfferById(offer.id).subscribe((response: any) => {
          this.offers = this.offers.filter(val => val.offerId !== offer.id);
          this.offer = Object.assign({}, this.offer);
          this.messageService.add({ severity: 'success', summary: 'Succés', detail: "L'offre a été supprimée avec succés", life: 1000 });
        })
      }
    });
  }


  findIndexById(id: string): number {
    let index = -1;
    for (let i = 0; i < this.offers.length; i++) {
      if (this.offers[i].offerId === id) {
        index = i;
        break;
      }
    }

    return index;
  }

  displayOfferModels(modelQuantities: ModelQuantity[]) {

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
}
