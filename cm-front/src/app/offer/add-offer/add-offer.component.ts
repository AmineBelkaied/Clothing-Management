import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { OfferService } from 'src/shared/services/offer.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';
import {Offer} from "src/shared/models/Offer";
import { Subject, takeUntil } from 'rxjs';
import { GlobalConf } from 'src/shared/models/GlobalConf';
import { GlobalConfService } from 'src/shared/services/global-conf.service';

@Component({
  selector: 'app-add-offer',
  templateUrl: './add-offer.component.html',
  styleUrls: ['./add-offer.component.css']
})
export class AddOfferComponent implements OnInit {

  offer: Offer;
  @Input()
  modelList: Model[] = [];
  @Input()
  editMode!: boolean;
  @Input()
  offerNameExists: boolean;

  @Output()
  submitEvent: EventEmitter<any> = new EventEmitter();
  @Output()
  formValidationEmitter = new EventEmitter();

  selectedModels: any[] = [];
  $unsubscribe: Subject<void> = new Subject();
  fbPages :FbPage[];
  offerForm: FormGroup;
  globalConf: GlobalConf;

  constructor(
    private fb: FormBuilder,
    private offerService: OfferService,
    public fbPageService: FbPageService,
    private globalConfService :GlobalConfService) {

    this.offerForm = this.fb.group({
      id: "",
      name: ['', Validators.required],
      price: [0, Validators.required],
      enabled: true,
      fbPages: [[], Validators.required],
      offerModels: this.fb.array([], Validators.required),
    })
   }

  ngOnInit(): void {
    this.globalConfService.getGlobalConfSubscriber().pipe(takeUntil(this.$unsubscribe)).subscribe(
      (globalConf: GlobalConf) => {
        this.globalConf = globalConf;
      }
    );
    this.offerService.offerSubscriber.pipe(takeUntil(this.$unsubscribe))
      .subscribe((offer:Offer) => this.offer = offer)
        this.fbPages = this.fbPageService.fbPages;

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
    } else {
      this.addModelQuantity();
    }
  }


   modelQuantities(): FormArray {
    return this.offerForm.get("offerModels") as FormArray
  }

  newModelQuantity(): FormGroup {
    return this.fb.group({
      model: [null, Validators.required],
      quantity: [1, [Validators.required, Validators.min(1)]],
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
    if(this.editMode) {
      if(block === 'Models'){
            this.offerService.updateOfferModels(this.offer.id,offer.offerModels).pipe(takeUntil(this.$unsubscribe))
            .subscribe(offerResponse => {
              offerResponse.offerModels= offer.offerModels;
              this.offerService.setOffer(offerResponse);
              this.offerService.spliceOffer();
              this.submitEvent.emit(offerResponse);
            });
      }
      if(block == 'FbPages'){
        this.offerService.updateOfferFbPages(this.offer.id,offer.fbPages).pipe(takeUntil(this.$unsubscribe))
        .subscribe(offerResponse => {
          this.offerService.setOffer(offerResponse);
          this.offerService.spliceOffer();
          this.submitEvent.emit(offerResponse);
        });
      }
      if(block === 'Offer'){
        offer.id = this.offer.id;
        this.offerService.updatOfferFields(offer).pipe(takeUntil(this.$unsubscribe))
        .subscribe(offerResponse => {
          this.submitEvent.emit(offerResponse);
        });
      }
    } else {
      this.offerService.addOffer(offer).pipe(takeUntil(this.$unsubscribe))
      .subscribe({
        next: offerResponse => {
          this.submitEvent.emit(offerResponse);
        },
        error: error => {
          console.error('There was an error!', error);
        }
      });
    }
  }

  checkOfferExistence(): void {
    this.formValidationEmitter.next(this.offerForm.get('name')?.value);
  }


  ngOnDestroy(): void {
    this.offerService.cleanOffre();
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
