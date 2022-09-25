import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { Offer } from 'src/shared/models/Offer';
import { Packet } from 'src/shared/models/Packet';
import { PacketService } from '../services/packet.service';

@Component({
  selector: 'app-add-packet',
  templateUrl: './add-packet.component.html',
  styleUrls: ['./add-packet.component.css']
})
export class AddPacketComponent implements OnInit {

  @Input() packet: any;

  @Input() offersList: any[] = [];

  @Input() editMode = false;

  @Input() modelDialog = false;

  @Output() submitEvent: EventEmitter<any> = new EventEmitter();

  selectedOffers: Offer[] = [];
  modelColors: any[] = [];
  modelSizes: any[] = [];
  offerModels: string[] = [];
  totalPrice: number = 0;
  sizes = ["S", "M", "L", "1", "2", "3", "4"];
  packetForm: FormGroup;
  constructor(private fb: FormBuilder, private cdRef: ChangeDetectorRef, private packetService: PacketService) {
    this.packetForm = this.fb.group({
      price: 0,
      offers: this.fb.array([])
    });
  }

  ngOnInit(): void {
    if (this.editMode) {
      this.packetService.findPacketRelatedProducts(this.packet.id)
        .subscribe((offers: any) => {
          console.log(offers);
          for (var i = 0; i < offers.length; i++) {
            /*let models = this.offerModels
            .filter((offerModel: any) => offers[i].name == offerModel.name)
            .map((result: any) => result.modelQuantites)*/
            this.offers().push(this.fb.group({
              name: offers[i].name,
              models: this.fb.array([])
            }));
            console.log();
            console.log(this.offers().at(i).value);
            let offer = offers[i];
            let nbrmodel = 0;
            // add models
            if (offer != null && offer.products != null && offer.products.length > 0)
              for (var j = nbrmodel; j < nbrmodel + offer.products.length; j++) {
                if (offer.products[j] != null && offer.products[j].model != null) {
                  let model = offer.products[j].model;
                  console.log("*************************************************");
                  console.log(model);
                  console.log("Model " + j);
                  console.log("-- START INSERTION OF MODEL ----")
                  this.addModel(i);
                  console.log(model.colors)
                  
                  console.log(model.sizes)
                  this.models(i).controls[j].get('colors')?.setValue(model.colors);
                  this.models(i).controls[j].get('sizes')?.setValue(model.sizes);
                  this.models(i).controls[j].get('name')?.setValue(model.name);
                  this.models(i).controls[j].get('reference')?.setValue(model.reference);
                  this.models(i).controls[j].get('selectedColor')?.setValue(offer.products[j].color);
                  this.models(i).controls[j].get('selectedSize')?.setValue(offer.products[j].size);
                  console.log("-- END INSERTION OF MODEL ----")
                } else //todo
                  this.clearModel(offer.name, i);
              }
            nbrmodel += offer.products.length;
            this.totalPrice += offers[i].price;
            this.packetForm.controls['price'].setValue(this.totalPrice);
          }
          this.packet.relatedProducts = this.packet.relatedProducts.split(',').join(' , ');
          console.log(this.offers().value);
        });
    } else {
      this.addOffer();
      console.log(this.packet)
    }
    console.log(this.offersList)
    console.log(this.packet.id)
  }

  ngAfterViewChecked() {
    //console.log( "! changement de la date du composant !" );

    this.cdRef.detectChanges();
  }

  offers(): FormArray {
    return this.packetForm.get("offers") as FormArray
  }

  models(offerIndex: number): FormArray {
    return this.offers().at(offerIndex).get("models") as FormArray
  }

  products(modelIndex: number, productIndex: number): FormArray {
    return this.models(modelIndex).at(productIndex).get("products") as FormArray
  }

  newOffer(): FormGroup {
    return this.fb.group({
      name: "",
      models: this.fb.array([])
    })
  }


  newModel(): FormGroup {
    let model = this.fb.group({
      // products: this.fb.array([]),
      name: '',
      reference: '',
      colors: [],
      sizes: [],
      selectedColor: '',
      selectedSize: ''
    })

    //console.log(model);
    return model;
  }

  addOffer() {
    this.offers().push(this.newOffer());
  }

  addModel(modelIndex: number) {
    this.models(modelIndex).push(this.newModel());
  }


  addProducts(offerName: any, index: number) {
    let nbrmodel = 0;
    this.models(index).clear();
    let offer: any = this.offersList.filter(off => off.name == offerName)[0];
    console.log(this.offers().at(index).value)
    this.offers().at(index).get('name')?.setValue(offer.name);
    console.log(offer)
    if (offer != null && offer.modelQuantities != null)
      for (var i = 0; i < offer.modelQuantities.length; i++) {
        let quantity = offer.modelQuantities[i].quantity;
        console.log("m ");
        console.log(offer.modelQuantities[i].sizes)
        console.log("Model " + i);
        console.log("qte : " + quantity)
        console.log("-- START INSERTION OF MODEL ----")
        for (var j = nbrmodel; j < nbrmodel + quantity; j++) {
          this.addModel(index);
          console.log("j : " + j);
          this.models(index).controls[j].get('colors')?.setValue(offer.modelQuantities[i].model.colors);
          this.models(index).controls[j].get('sizes')?.setValue(offer.modelQuantities[i].model.sizes);
          this.models(index).controls[j].get('name')?.setValue(offer.modelQuantities[i].model.name);
          this.models(index).controls[j].get('reference')?.setValue(offer.modelQuantities[i].model.reference);
        }
        nbrmodel += quantity;
        console.log("-- END INSERTION OF MODEL ----")
      }

    this.totalPrice += offer.price;
    this.packetForm.controls['price'].setValue(this.totalPrice);
  }

  removeOffer(offer: any, i: number) {
    this.offers().removeAt(i);
    this.totalPrice = this.totalPrice - offer.price;
  }

  clearModel(offerName: any, index: number) {
    this.models(index).clear();
    let nbrmodel = 0;
    let offer: any = this.offersList.filter(off => off.name == offerName)[0];
    console.log(this.offers().at(index).value)
    this.offers().at(index).get('name')?.setValue(offer.name);
    console.log(offer)
    if (offer != null && offer.modelQuantities != null)
      for (var i = 0; i < offer.modelQuantities.length; i++) {
        let quantity = offer.modelQuantities[i].quantity;
        console.log("Model " + i);
        console.log("qte : " + quantity)
        console.log("-- START INSERTION OF MODEL ----")
        for (var j = nbrmodel; j < nbrmodel + quantity; j++) {
          this.addModel(index);
          console.log("j : " + j);
          this.models(index).controls[j].get('colors')?.setValue(offer.modelQuantities[i].model.colors);
          this.models(index).controls[j].get('sizes')?.setValue(offer.modelQuantities[i].model.sizes);
          this.models(index).controls[j].get('name')?.setValue(offer.modelQuantities[i].model.name);
          this.models(index).controls[j].get('reference')?.setValue(offer.modelQuantities[i].model.reference);
        }
        nbrmodel += quantity;
        console.log("-- END INSERTION OF MODEL ----")
      }
  }

  createProductRef(refModel: any, color: any, size: any) {
    console.log(refModel)
    console.log(color)
    console.log(size)
    let ref = (refModel != null && refModel != "") ? refModel : "?";
    let colorModel = (color != null && color != "") ? color : "?";
    let sizeModel = (size != null && size != "") ? size : "?";
    let productRef = ref + colorModel + sizeModel;
    console.log(productRef);
    return productRef;
  }

  /*   removeProduct(offerIndex: number, productIndex: number) {
      this.products(offerIndex).removeAt(productIndex);
    } */

  onSubmit() {
    let productsRef: string[] = [];
    let packetReference = "";
    let refsArray =[];
    let refs = "";
    console.log("this.packetForm.value", this.packetForm.value);
    let packet = this.packetForm.value;
    for (var i = 0; i < packet.offers.length; i++) {
      let offer = packet.offers[i];
      if (offer.name != null)
        packetReference += offer.name + ":"
      if (offer.models.length > 0) {
        for (var j = 0; j < offer.models.length; j++) {
          let refModel = offer.models[j].reference;
          let color = offer.models[j].selectedColor != null ? offer.models[j].selectedColor.reference : null;
          let size = offer.models[j].selectedSize != null ? offer.models[j].selectedSize.reference : null;
          let productRef = this.createProductRef(refModel, color, size);
            productsRef.push(productRef);
            packetReference += productRef;
          if (j < offer.models.length - 1)
            packetReference += ",";
        }
      }
      if (i < packet.offers.length - 1)
        packetReference += "-";
    }
    console.log(refs);
    console.log("Ref", productsRef);
    console.log("packetReference", packetReference);
    let selectedProducts = { "idPacket": this.packet.id, "price": this.totalPrice, "productsRef": productsRef, "packetRef": packetReference };
    this.packetService.addProductsToPacket(selectedProducts)
      .subscribe(() => {
        console.log("nice !")
        let result = { "packet": selectedProducts, "modelDialog": false }
        this.submitEvent.emit(result);
      });
  }



}
