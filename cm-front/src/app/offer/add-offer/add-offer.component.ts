import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { OfferService } from 'src/shared/services/offer.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';
import {Offer} from "src/shared/models/Offer";
import { Subject, takeUntil } from 'rxjs';

@Component({
  selector: 'app-add-offer',
  templateUrl: './add-offer.component.html',
  styleUrls: ['./add-offer.component.css']
})
export class AddOfferComponent implements OnInit {

  offer: Offer;
  @Input() modelList: Model[] = [];
  @Input() editMode!: boolean;
  @Output() submitEvent: EventEmitter<any> = new EventEmitter();
  selectedModels: any[] = [];
  $unsubscribe: Subject<void> = new Subject();
  fbPages :FbPage[];
  offerForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private offerService: OfferService,
    public fbPageService: FbPageService) {
    this.offerForm = this.fb.group({
      id: "",
      name: '',
      price: 0,
      enabled: false,
      fbPages: [],
      offerModels: this.fb.array([]),
    })
   }



  ngOnInit(): void {
    this.offerService.offerSubscriber.pipe(takeUntil(this.$unsubscribe))
      .subscribe((offer:Offer) => this.offer = offer)
    console.log("this.offer",this.offer);

    this.fbPageService.getFbPagesSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (fbPages: FbPage[]) => {
        this.fbPages = fbPages;
      }
    );
    console.log(this.offer)

    if(this.editMode) {
      this.offerForm.get('offerId')?.setValue(this.offer.id);
      this.offerForm.get('name')?.setValue(this.offer.name);
      this.offerForm.get('price')?.setValue(this.offer.price);
      this.offerForm.get('enabled')?.setValue(this.offer.enabled);
      this.offerForm.get('fbPages')?.setValue(this.offer.fbPages);
      if(this.offer.offerModels.length > 0 )
        for(let i=0 ; i < this.offer.offerModels.length ; i++) {
            this.addModelQuantity();
            this.modelQuantities().at(i).get('model')?.setValue(this.offer.offerModels[i].model);
            this.modelQuantities().at(i).get('quantity')?.setValue(this.offer.offerModels[i].quantity);
        }
    }
  }


   modelQuantities(): FormArray {
    return this.offerForm.get("offerModels") as FormArray
  }

  newModelQuantity(): FormGroup {
    return this.fb.group({
      model: null,
      quantity: 0,
    })
  }

  addModelQuantity() {
    this.modelQuantities().push(this.newModelQuantity());
  }

  removeModelQuantity(index: any) {
    this.modelQuantities().removeAt(index);
  }

  saveOffer(block:String) {
    let offer: Offer = this.offerForm.value;
    console.log("offer",offer);
    if(this.editMode) {

      if(block == "Models"){
        this.offerService.updateOfferModels(this.offer.id,offer.offerModels).pipe(takeUntil(this.$unsubscribe))
        .subscribe(offerResponse => {
          console.log(offerResponse);
          offerResponse.offerModels= offer.offerModels;
          this.offerService.setOffer(offerResponse);
          this.offerService.spliceOffer();
          this.submitEvent.emit(offerResponse);
        });
      }
      if(block == "FbPages"){
        this.offerService.updateOfferFbPages(this.offer.id,offer.fbPages).pipe(takeUntil(this.$unsubscribe))
        .subscribe(offerResponse => {
          this.offerService.setOffer(offerResponse);
          this.offerService.spliceOffer();
          this.submitEvent.emit(offerResponse);
        });
      }
      if(block == "Offer"){
        console.log("offer",offer);
        offer.id=this.offer.id;
        this.offerService.updateData(offer).pipe(takeUntil(this.$unsubscribe))
        .subscribe(offerResponse => {
          console.log(offerResponse);
          //this.offerService.setOffer(offerResponse)
          //this.offerService.spliceOffer();
          this.submitEvent.emit(offerResponse);
        });
      }


    } else {
      console.log("addmode",offer);

      this.offerService.addOffer(offer).pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: offerResponse => {
          console.log(offerResponse);
          //this.offerService.pushOffer(offerResponse)
          this.submitEvent.emit(offerResponse);
        },
        error: error => {
          console.error('There was an error!', error);
        }
      });
    }
  }

  ngOnDestroy(): void {
    this.offerService.cleanOffre();
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
