import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Model } from 'src/shared/models/Model';
import { Offer } from 'src/shared/models/Offer';
import { ModelService } from '../../../shared/services/model.service';
import { OfferService } from '../../../shared/services/offer.service';
import { FbPage } from 'src/shared/models/FbPage';
import { of, Subject, takeUntil } from 'rxjs';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';

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

  selectedActionsOptions: { name: string; label: string; }[];
  actionsOptions: { name: string; label: string; }[];
  isOptionsDialogVisible: boolean = false;

  trackByFunction = (index: any, item: { id: any }) => {
    return item.id;
  };

  clonedOffer: Offer;
  offerNameExists: boolean;
  globalConf: GlobalConf;

  constructor(
    private offerService: OfferService,
    private modelService: ModelService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService,
    private globalConfService :GlobalConfService) {
  }

  ngOnInit() {
    this.globalConfService.getGlobalConfSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (globalConf: GlobalConf) => {
        this.globalConf = globalConf;
      }
    );
    console.log("init list-offers");
    this.offerService.getOffersSubscriber()
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: (offerList: any) => {
          if (offerList.length > 0)
            this.offers = offerList.filter((offer: Offer) => !offer.deleted);
        },
        error: (err: any) => {
          console.error('Error fetching offers:', err);
        }
      });

    this.modelService.loadModels().pipe(takeUntil(this.$unsubscribe)).subscribe((modelList: any) => {
      this.models = modelList;
    });

    this.actionsOptions = [
      { name: 'showArchivedOffers', label: 'Offres archivés' },
      { name: 'showEnabledOffers', label: 'Offres activés' },
      { name: 'showDisabledOffers', label: 'Offres désactivés' }
    ];
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
          .subscribe(() => {
            console.log("offers successfully deleted !");
            this.offers = this.offers.filter((offer: Offer) => selectedOffersId.indexOf(offer.id) == -1);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les offres séléctionnés ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }

  /*   displayPages(fbPagesIds: number[]) : FbPage[]{
      return this.getFbPages(fbPagesIds);
    } */

  editOffer(offer: any) {
    this.offerService.setOffer(offer);
    this.offerDialog = true;
    this.editMode = true;
    this.clonedOffer = { ...offer };
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

    this.offerService.checkOfferUsage(offer.id!)
      .subscribe((offerUsageNumber: number) => {

        this.confirmationService.confirm({
          message: this.getOfferConfirmationMessage(offerUsageNumber, offer.name),
          header: 'Confirmation',
          icon: 'pi pi-exclamation-triangle',
          accept: () => {
            this.offerService
              .deleteOfferById(offer.id!, offerUsageNumber > 0)
              .pipe(takeUntil(this.$unsubscribe))
              .subscribe(() => {
                this.offers = this.offers.filter(val => val.id !== offer.id);
                this.offerService.setOffers(this.offers);
                //this.offer = Object.assign({}, this.offer);
                this.messageService.add({ severity: 'success', summary: 'Succés', detail: "L'offre a été supprimée avec succés", life: 1000 });
              });
          },
        });
      });
  }

  rollBackOffer(offer: Offer) {
    this.confirmationService.confirm({
      message: 'Etes-vous sûr de bien vouloir restaurer cette offre?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.offerService
          .rollBackOffer(offer.id!)
          .pipe(takeUntil(this.$unsubscribe))
          .subscribe(() => {
            this.offers = this.offers.filter(val => val.id !== offer.id);
            //this.offerService.setOffers(this.offers);
            this.messageService.add({
              severity: 'success',
              summary: 'Successful',
              detail: 'Offre restaurée avec succès',
              life: 3000,
            });
          });
      },
    });
  }


  private getOfferConfirmationMessage(offerUsageNumber: number, offerName: string) {
    return offerUsageNumber ? `<p>L'offre <strong>${offerName}</strong> est utilisé
                  <strong style="color: red;">${offerUsageNumber} fois</strong> au niveau des commandes.</p>
                  <p style="font-weight: bold; margin-top: 10px;">Etes-vous sûr de bien vouloir l'archiver ?</p>` :

      `<p>L'offre <strong>${offerName}</strong> n'est pas encore utilisé` +
      `<p style="font-weight: bold; margin-top: 10px;">Etes-vous sûr de bien vouloir le supprimer ?</p>`
  }

  onOptionSelect(): void {
    const filters: { [key: string]: (offer: Offer) => boolean } = {
      'showArchivedOffers': (offer) => offer.deleted,
      'showEnabledOffers': (offer) => offer.enabled,
      'showDisabledOffers': (offer) => !offer.enabled && !offer.deleted,
    };

    this.offers = this.selectedActionsOptions.length
      ? this.selectedActionsOptions.flatMap(selectedAction =>
        this.offerService.offers.filter(filters[selectedAction.name] || (() => true))
      )
      : this.offerService.offers.filter((offer: Offer) => !offer.deleted);
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

  checkOfferExistence(offerName: string): void {
    this.offerNameExists = this.editMode ?
      this.offers.filter((offer: Offer) => offer.name !== this.clonedOffer.name).some((offer: Offer) => offer.name.toLowerCase() === offerName.toLowerCase()) :
      this.offers.some((offer: Offer) => offer.name.toLowerCase() === offerName.toLowerCase());
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
      console.log("offerResponse", offerResponse);

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
