import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { Offer } from 'src/shared/models/Offer';
import { Packet } from 'src/shared/models/Packet';
import { PacketService } from '../../../shared/services/packet.service';

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
  packetReference = "";
  packetDescription = "";
  relatedProducts = "";
  packetForm: FormGroup;
  packetPrice: any;
  constructor(private fb: FormBuilder, private cdRef: ChangeDetectorRef, private packetService: PacketService) {
    this.packetForm = this.fb.group({
      totalPrice: 0,
      deliveryPrice: 7,
      discount: 0,
      offers: this.fb.array([])
    });
  }

  ngOnInit(): void {
    if (this.editMode) {
      this.packetService.findPacketRelatedProducts(this.packet.id)
        .subscribe((packet: any) => {
          console.log(packet);
          let offers = packet.offerUpdateDTOList;
          for (var i = 0; i < offers.length; i++) {
            /*let models = this.offerModels
            .filter((offerModel: any) => offers[i].name == offerModel.name)
            .map((result: any) => result.modelQuantites)*/
            this.offers().push(this.fb.group({
              offerId: offers[i].offerId,
              name: offers[i].name,
              price: offers[i].price,
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
                  this.getPacketDescription(model.name , offer.products[j].color.name, offer.products[j].size.reference);
                  this.relatedProducts += this.createProductRef(model.reference, offer.products[j].color.reference, offer.products[j].size.reference);
                  console.log("-- END INSERTION OF MODEL ----")
                } else //todo
                  this.clearModel(offer.name, i);
              }
            nbrmodel += offer.products.length;
          }
          this.totalPrice = packet.totalPrice;
          this.packetForm.controls['totalPrice'].setValue(packet.totalPrice);
          this.packetForm.controls['deliveryPrice'].setValue(packet.deliveryPrice);
          this.packetForm.controls['discount'].setValue(packet.discount);
          this.packet.relatedProducts = this.packet.relatedProducts.split(',').join(' , ');
          this.packetDescription = this.packetDescription.substring(0, this.packetDescription.length - 3);
          this.relatedProducts = this.relatedProducts.substring(0, this.relatedProducts.length - 3);
          this.calculateTotalPrice();
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
      offerId: null,
      name: "",
      price: 0,
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
    if (offer != null && offer.modelQuantities != null) {
      console.log(this.offers().at(index).value)
      this.offers().at(index).get('offerId')?.setValue(offer.offerId);
      this.offers().at(index).get('name')?.setValue(offer.name);
      this.offers().at(index).get('price')?.setValue(offer.price);
      console.log(offer)
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
          this.createPacketDescription();
        }
        nbrmodel += quantity;
        console.log("-- END INSERTION OF MODEL ----")
      }
    }
    this.totalPrice = 0;
    for (var i = 0; i < this.offers().length; i++) {
      this.totalPrice += this.offers().at(i).get('price')?.value;
    }
    this.packetForm.controls['totalPrice'].setValue(this.totalPrice);
    this.calculateTotalPrice();
  }

  removeOffer(offer: any, i: number) {
    console.log(this.totalPrice);
    console.log(offer.value.price);
    this.offers().removeAt(i);
    this.totalPrice = (offer.value.price != undefined && offer.value.price != null) ? this.totalPrice - offer.value.price : 0;
    this.packetForm.controls['totalPrice'].setValue(this.totalPrice);
    this.calculateTotalPrice();
    this.createPacketDescription();
    console.log(this.totalPrice);
  }

  clearModel(offerName: any, index: number) {
    this.models(index).clear();
    let nbrmodel = 0;
    let offer: any = this.offersList.filter(off => off.name == offerName)[0];
    console.log(offer)
    if (offer != null && offer.modelQuantities != null) {
      console.log(this.offers().at(index).value)
      this.offers().at(index).get('offerId')?.setValue(offer.offerId);
      this.offers().at(index).get('name')?.setValue(offer.name);
      this.offers().at(index).get('price')?.setValue(offer.price);
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
/*     this.totalPrice = (offer.value.price != undefined && offer.value.price != null) ? this.totalPrice - offer.value.price : 0;
    this.packetForm.controls['totalPrice'].setValue(this.totalPrice);
    this.calculateTotalPrice(); */
  }

  createProductRef(modelRef: any, colorRef: any, sizeRef: any) {
    let ref = (modelRef != null && modelRef != "") ? modelRef : "?";
    let colorModel = (colorRef != null && colorRef != "") ? colorRef : "?";
    let sizeModel = (sizeRef != null && sizeRef != "") ? sizeRef : "?";
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
    this.packetDescription = "";
    let refsArray = [];
    let refs = "";
    console.log("this.packetForm.value", this.packetForm.value);
    let packet = this.packetForm.value;
    for (var i = 0; i < packet.offers.length; i++) {
      let offer = packet.offers[i];
      if (offer.offerId != null && offer.offerId != undefined) {
        packetReference += offer.offerId + ":"
        if (offer.models.length > 0) {
          for (var j = 0; j < offer.models.length; j++) {
            let modelRef = offer.models[j].reference;
            let colorRef = offer.models[j].selectedColor != null ? offer.models[j].selectedColor.reference : null;
            let sizeRef = offer.models[j].selectedSize != null ? offer.models[j].selectedSize.reference : null;
            let productRef = this.createProductRef(modelRef, colorRef, sizeRef);
            productsRef.push(productRef);
            packetReference += productRef;
            if (j < offer.models.length - 1)
              packetReference += ",";
            let modelName = offer.models[j].name != null ? offer.models[j].name : null;
            let colorName = offer.models[j].selectedColor != null ?  offer.models[j].selectedColor?.name : null;
            this.getPacketDescription(modelName , colorName, sizeRef)
          }
        }
      }
      if (i < packet.offers.length - 1)
        packetReference += "-";
    }
    this.packetDescription = this.packetDescription.substring(0, this.packetDescription.length - 2);
    console.log(refs);
    console.log("Ref", productsRef);
    console.log("packetReference", packetReference);
    let selectedProducts = { "idPacket": this.packet.id, "totalPrice": this.totalPrice, "productsRef": productsRef, "packetDescription" : this.packetDescription, "deliveryPrice": packet.deliveryPrice, "discount": packet.discount, "packetRef": packetReference };
    this.packetService.addProductsToPacket(selectedProducts)
      .subscribe(() => {
        console.log("nice !")
        let result = { "packet": selectedProducts, "modelDialog": false }
        this.submitEvent.emit(result);
      });
  }

  createPacketDescription() {
    this.packetDescription = "";
    this.relatedProducts = "";
    let packet = this.packetForm.value;
    for (var i = 0; i < packet.offers.length; i++) {
      let offer = packet.offers[i];
      if (offer.offerId != null && offer.offerId != undefined) {
        if (offer.models.length > 0) {
          for (var j = 0; j < offer.models.length; j++) {
            let modelRef = offer.models[j].reference;
            let colorRef = offer.models[j].selectedColor != null ? offer.models[j].selectedColor.reference : null;
            let sizeRef = offer.models[j].selectedSize != null ? offer.models[j].selectedSize.reference : null;
            this.relatedProducts += this.createProductRef(modelRef, colorRef, sizeRef) + " , ";
            let modelName = offer.models[j].name != null ? offer.models[j].name : null;
            let colorName = offer.models[j].selectedColor != null ?  offer.models[j].selectedColor?.name : null;
            this.getPacketDescription(modelName , colorName, sizeRef)
          }
        }
      }
    }
    this.packetDescription = this.packetDescription.substring(0, this.packetDescription.length - 3);
    this.relatedProducts = this.relatedProducts.substring(0, this.relatedProducts.length - 3);
  }

  getPacketDescription(modelName?: any, color?: any, size?: any) {
    if (modelName != null)
      this.packetDescription += modelName;
    if (color != null)
      this.packetDescription += " " + color;
    if (size != null)
      this.packetDescription += " (" + size + ")";
    this.packetDescription += " , ";
  }

  calculateTotalPrice() {
    this.packetPrice = this.totalPrice + this.packetForm.controls['deliveryPrice'].value - this.packetForm.controls['discount'].value;
  }

}
