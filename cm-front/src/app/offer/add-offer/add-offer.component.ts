import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { Model } from 'src/shared/models/Model';
import { Offer } from 'src/shared/models/Offer';
import { OfferModelDTO } from 'src/shared/models/OfferModelDTO';
import { OfferService } from '../../../shared/services/offer.service';
import { FbPage } from 'src/shared/models/FbPage';

@Component({
  selector: 'app-add-offer',
  templateUrl: './add-offer.component.html',
  styleUrls: ['./add-offer.component.css']
})
export class AddOfferComponent implements OnInit {
earnPercentage(_t55: number) {
throw new Error('Method not implemented.');
}


  @Input() offerModelDTO: OfferModelDTO = {
    "offerId": "",
    "name": "",
    "modelQuantities": [],
    "fbPages" : [],
    "price": 0,
    "enabled": false,
  }
  @Input() modelList: Model[] = [];
  @Input() fbPages: FbPage[] = [];
  @Input() editMode!: boolean;
  @Output() submitEvent: EventEmitter<any> = new EventEmitter();
  selectedModels: any[] = [];

  offerForm: FormGroup;
  constructor(private fb: FormBuilder, private offerService: OfferService, private messageService: MessageService) {
    this.offerForm = this.fb.group({
      offerId: "",
      name: '',
      price: 0,
      enabled: false,
      fbPages: [],
      modelQuantities: this.fb.array([]),
      earnCoefficient: 0
    })
   }


  ngOnInit(): void {
    console.log(this.offerModelDTO)
    console.log(this.fbPages);

    if(this.editMode) {
      this.offerForm.get('offerId')?.setValue(this.offerModelDTO.offerId);
      this.offerForm.get('name')?.setValue(this.offerModelDTO.name);
      this.offerForm.get('price')?.setValue(this.offerModelDTO.price);
      this.offerForm.get('enabled')?.setValue(this.offerModelDTO.enabled);
      this.offerForm.get('fbPages')?.setValue(this.offerModelDTO.fbPages);
      if(this.offerModelDTO.modelQuantities != null && this.offerModelDTO.modelQuantities.length > 0 )
        for(var i=0 ; i < this.offerModelDTO.modelQuantities.length ; i++) {
            this.addModelQuantity();
            this.modelQuantities().at(i).get('model')?.setValue(this.offerModelDTO.modelQuantities[i].model);
            this.modelQuantities().at(i).get('quantity')?.setValue(this.offerModelDTO.modelQuantities[i].quantity);
            this.modelQuantities().at(i).get('modelEarnCoefficient')?.setValue(this.offerModelDTO.modelQuantities[i].modelEarnCoefficient);
        }
    }
  }


   modelQuantities(): FormArray {
    return this.offerForm.get("modelQuantities") as FormArray
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

  saveOffer() {
    let offerModelDTO: OfferModelDTO = this.offerForm.value;
    console.log(offerModelDTO);
    if(this.editMode) {
      this.offerService.updateOffer(offerModelDTO)
      .subscribe(offerResponse => {
        console.log(offerResponse);
        this.submitEvent.emit(offerResponse);
      });
    } else {
      this.offerService.addOffer(offerModelDTO)
      .subscribe({
        next: offerResponse => {
          console.log(offerResponse);
          this.submitEvent.emit(offerResponse);
        },
        error: error => {
          console.error('There was an error!', error);
        }
      });
    }
  }
 /*    this.submitted = true;

    if (this.offer.name.trim()) {
      if (this.offer.id) {
        this.offerService.updateOffer(this.offer)
          .subscribe({
            next: response => {
              console.log(response);
              this.offers[this.findIndexById(this.offer.id)] = this.offer;
              this.messageService.add({ severity: 'success', summary: 'Successful', detail: "L'offre a été mis à jour avec succés", life: 1000 });
            }
          });
      }
      else {
        console.log(this.offer.models)
        //this.model.colors = this.model.colors.map((color: any) => {return  {"code" : color.code , "name" : color.name}});
        this.offerService.addOffer(this.offer)
          .subscribe({
            next: response => {
              console.log(response);
              //this.model.id = this.createId();
              //this.model.image = 'model-placeholder.svg';
              this.offers.push(this.offer);
              this.messageService.add({ severity: 'success', summary: 'Successful', detail: "L'offre a été crée avec succés", life: 1000 });
            },
            error: error => {

              console.error('There was an error!', error);
            }
          })
      }
      this.offers = [...this.offers];
      this.offerDialog = false;
      this.offer = Object.assign({}, this.offer);
    } */

}
