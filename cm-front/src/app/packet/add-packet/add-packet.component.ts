import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { PacketService } from '../../../shared/services/packet.service';
import { ProductOfferDTO } from 'src/shared/models/ProductOfferDTO';
import { StringUtils } from 'src/shared/utils/string-utils';
import { Color } from 'src/shared/models/Color';
import { Size } from 'src/shared/models/Size';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ENDED, NON_CONFIRMEE } from 'src/shared/utils/status-list';

@Component({
  selector: 'app-add-packet',
  templateUrl: './add-packet.component.html',
  styleUrls: ['./add-packet.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class AddPacketComponent implements OnInit {
  @Input() packet: any;

  @Input() offersList: any[] = [];

  @Input() editMode: boolean = false;

  @Input() modelDialog: boolean = false;

  @Output() submitEvent: EventEmitter<any> = new EventEmitter();

  enableFakeSize : boolean = false;
  private selectedOffer: any;
  totalPrice: number = 0;
  packetDescription: string = '';
  productReferences: string = '';
  packetForm: FormGroup;
  packetPrice: number = 0;
  selectedSizeReel : string = '';
  noChoiceColor!: Color;
  noChoiceSize!: Size;
  stockAvailable: number;
  colorSizeChoosen: boolean = true;


  constructor(private fb: FormBuilder, private cdRef: ChangeDetectorRef, private packetService: PacketService,private confirmationService: ConfirmationService, private messageService: MessageService) {
    this.packetForm = this.fb.group({
      totalPrice: 0,
      deliveryPrice: 7,
      discount: 0,
      offers: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.editMode ? this.getSelectedProducts() : this.addOffer();
    this.stockAvailable = 1;
  }

  ngAfterViewChecked(): void {
    this.cdRef.detectChanges();
  }

  offers(): FormArray {
    return this.packetForm.get('offers') as FormArray
  }

  models(offerIndex: number): FormArray {
//    console.log("this.offers()",this.offers().at(offerIndex).get('models') as FormArray);

    return this.offers().at(offerIndex).get('models') as FormArray
  }

  products(modelIndex: number, productIndex: number): FormArray {
    return this.models(modelIndex).at(productIndex).get('products') as FormArray
  }

  newOffer(): FormGroup {
    return this.fb.group({
      offerId: null,
      name: '',
      price: 0,
      models: this.fb.array([])
    })
  }

  getSelectedProducts(): void {
    this.packetService.findPacketRelatedProducts(this.packet.id)
      .subscribe((packet: any) => {
        //console.log(packet);

        let offers = packet.offerUpdateDTOList;
        for (var index = 0; index < offers.length; index++) {
          this.addSelectedOffer(offers[index].offerId, offers[index].name, offers[index].price);
          this.addSelectedModels(offers[index], index);
        }
        this.totalPrice = packet.totalPrice;
        this.packetForm.controls['totalPrice'].setValue(packet.totalPrice);
        this.packetForm.controls['deliveryPrice'].setValue(packet.deliveryPrice);
        this.packetForm.controls['discount'].setValue(packet.discount);
        this.packetDescription = StringUtils.removeChars(this.packetDescription, 2);
        this.productReferences = StringUtils.removeChars(this.productReferences, 2);
        this.calculatePacketPrice();
      });
  }


  newModel(): FormGroup {
    let model = this.fb.group({
      name: '',
      reference: '',
      colors: [],
      sizes: [],
      selectedColor: '',
      selectedSize: '',
      selectedProduct: '',
      image: '',
      bytes: '',
      selectedSizeReel:''
    })
    return model;
  }

  addOffer(): void {

    this.offers().push(this.newOffer());
  }

  addSelectedOffer(offerId: number, name: string, price: number): void {
    this.offers().push(
      this.fb.group({
        offerId: offerId,
        name: name,
        price: price,
        models: this.fb.array([])
      }
      ));
  }

  addModel(modelIndex: number): void {
    this.models(modelIndex).push(this.newModel());
  }

  addSelectedModels(offer: any, offerIndex: number): void {
    let nbrModel = 0;
    if (offer != null && offer.products != null && offer.products.length > 0)
      for (var j = nbrModel; j < nbrModel + offer.products.length; j++) {
        if (offer.products[j] != null && offer.products[j].model != null) {
          let model = offer.products[j].model;
          this.addModel(offerIndex);
          this.setModelControlValues(this.models(offerIndex).controls[j], model);
          this.setProductControlValues(this.models(offerIndex).controls[j], offer.products[j]);
          //console.log('addSelectedModels');
          this.setPacketDescription(model.name, offer.products[j].color.name, offer.products[j].size.reference);
          //this.productReferences += this.createProductRef(model.reference, offer.products[j].color.reference, offer.products[j].size.reference).concat(' , ');
        }
      }
    nbrModel += offer.products.length;
  }


  setOfferControlValues(offerControl: AbstractControl, offerValue: any): void {
    this.setControlValue(offerControl, 'offerId', offerValue.offerId);
    this.setControlValue(offerControl, 'name', offerValue.name);
    this.setControlValue(offerControl, 'price', offerValue.price);
  }

  setModelControlValues(modelControl: AbstractControl, modelValue: Model): void {
    //console.log(modelValue);

    this.noChoiceColor = modelValue.colors.find((color: Color) => color.reference === "?");
    let colors = modelValue.colors.filter((color: Color) => color.reference != "?");
    this.setControlValue(modelControl, 'colors', colors);

    this.noChoiceSize = modelValue.sizes.find((size: Size) => size.reference === "?");
    let sizes = modelValue.sizes.filter((size: Size) => size.reference != "?");
    this.setControlValue(modelControl, 'sizes', sizes);
    this.setControlValue(modelControl, 'name', modelValue.name);
    this.setControlValue(modelControl, 'reference', modelValue.reference);
    this.setControlValue(modelControl, 'image', modelValue.bytes);
    //console.log("modelValue.products",modelValue.products);

    let defaultProduct = modelValue.products?.find((product: any) => product.color.reference === "?" && product.size.reference === "?" );
    //console.log("defaultProduct",defaultProduct);

    this.setControlValue(modelControl, 'selectedProduct', defaultProduct);
  }

  setProductControlValues(productControl: AbstractControl, productValue: any): void {
    this.setControlValue(productControl, 'selectedProduct', productValue);
    this.setControlValue(productControl, 'selectedColor', productValue.color);
    this.setControlValue(productControl, 'selectedSize', productValue.size);
  }

  setControlValue(control: AbstractControl, controlName: string, controlValue: any): void {
    control.get(controlName)?.setValue(controlValue);
  }

  onOfferChange(offerId: string, index: number): void {
    console.log("offerId",offerId);

    this.models(index).clear();

    this.selectedOffer = this.offersList.find(off => off.name === offerId);
    //console.log("this.selectedOffer",this.selectedOffer);

    if (this.selectedOffer != null && this.selectedOffer.models.length > 0) {
      this.setOfferModelsValues(index);
    }
    this.calculateTotalPrice();
    this.calculatePacketPrice();
  }

  setOfferModelsValues(index: number): void {
    //console.log("this.offers().at(index)",this.offers().at(index));

    this.setOfferControlValues(this.offers().at(index), this.selectedOffer);
    for (var i = 0; i < this.selectedOffer.models.length; i++) {
      this.addModel(index);
      this.setModelControlValues(this.models(index).controls[i], this.selectedOffer.models[i]);
      this.createPacketDescription();
    }
  }

  clearModel(offerName: any, index: number): void {
    this.models(index).clear();
    let offer: any = this.offersList.find(off => off.name == offerName);
    if (offer != null && offer.models.length > 0) {
      this.setOfferControlValues(this.offers().at(index), offer);
      for (var i = 0; i < offer.models.length; i++) {
          this.addModel(index);
          this.setModelControlValues(this.models(index).controls[i], offer.models[i]);
        }
      }
  }

  setSelectedProductValue(selectedModel: AbstractControl, index: number, selectedOffer: AbstractControl): void {
    if (this.editMode)
      this.selectedOffer = this.offersList.find(offer => offer.offerId == selectedOffer.get('offerId')?.value);
      this.setNoChoiceColorSize(selectedModel, index);
      let selectedProduct = this.selectedOffer.models[index].products.find((product: any) => product.color.id == selectedModel.get('selectedColor')?.value.id && product.size.id == selectedModel.get('selectedSize')?.value.id);
      selectedModel.get('selectedProduct')?.setValue(selectedProduct);
      //if(selectedProduct.quantity<1)this.stockDisponible=false;
      //console.log("this.stockDisponible",this.stockDisponible);
      this.createPacketDescription();
  }

  setNoChoiceColorSize(selectedModel: AbstractControl, index: number): void {
    if (!selectedModel.get('selectedColor')?.value) {
      let noChoiceColor = this.selectedOffer.models[index].colors.find((color: Color) => color.reference == "?");
      selectedModel.get('selectedColor')?.setValue(noChoiceColor);
    }
    if (!selectedModel.get('selectedSize')?.value) {
      let noChoiceSize = this.selectedOffer.models[index].sizes.find((size: Size) => size.reference == "?");
      selectedModel.get('selectedSize')?.setValue(noChoiceSize);
    }
  }

  removeOffer(offer: any, i: number): void {
    this.offers().removeAt(i);
    this.totalPrice = (offer.value.price != undefined && offer.value.price != null) ? this.totalPrice - offer.value.price : 0;
    this.packetForm.controls['totalPrice'].setValue(this.totalPrice);
    this.calculatePacketPrice();
    this.createPacketDescription();
  }

/*
  createProductRef(modelRef: any, colorRef: any, sizeRef: any) {
    let ref = (modelRef != null && modelRef != '') ? modelRef : '?';
    let colorModel = (colorRef != null && colorRef != '') ? colorRef : '?';
    let sizeModel = (sizeRef != null && sizeRef != '') ? sizeRef : '?';
    let productRef = ref.concat(colorModel).concat(sizeModel);
    return productRef;
  } */

  onSubmit(event:Event) {

    let productsOffers: ProductOfferDTO[] = this.prepareProductsOffers(this.packetForm.value);
    console.log("this.stockAvailable:",this.stockAvailable);
    if(this.stockAvailable<1){
      this.enableFakeSize = true;
      this.confirmationService.confirm({

        target: event.target as EventTarget,
        message: this.stockAvailable<0?'Veiller remplir tous les champs':'Stock 0-Veiller le remplacer par un fake size.',
        icon: 'pi pi-exclamation-circle',
        acceptIcon: 'pi pi-check mr-1',
        rejectIcon: 'pi pi-times mr-1',
        rejectButtonStyleClass: 'p-button-danger p-button-sm',
        acceptButtonStyleClass: 'p-button-outlined p-button-sm',
        accept: () => {
            this.messageService.add({ severity: 'info', summary: 'Confirmed', detail: 'Veiller remplir fake size', life: 3000 });
        },
        reject: () => {
            this.submitProductsOffers(productsOffers,this.stockAvailable);
            this.messageService.add({ severity: 'error', summary: 'Rejected', detail: 'You have rejected', life: 3000 });
        }
      });
    }
    else
      {
        this.submitProductsOffers(productsOffers,this.stockAvailable);
      }

  }

  submitProductsOffers(productsOffers: ProductOfferDTO[],stock:number){
    //console.log("submitProductsOffers");
    let selectedProducts ={};
    if(stock)
      selectedProducts = { 'idPacket': this.packet.id, 'totalPrice': this.totalPrice, 'productsOffers': productsOffers, 'packetDescription': this.packetDescription, 'deliveryPrice': this.packetForm.value.deliveryPrice, 'discount': this.packetForm.value.discount,'status': NON_CONFIRMEE};
    else
      selectedProducts = { 'idPacket': this.packet.id, 'totalPrice': this.totalPrice, 'productsOffers': productsOffers, 'packetDescription': this.packetDescription, 'deliveryPrice': this.packetForm.value.deliveryPrice, 'discount': this.packetForm.value.discount,'status': ENDED };

    this.packetService.addProductsToPacket(selectedProducts,this.stockAvailable)
      .subscribe((packet: any) => {
        //console.log("addProductsToPacket");
        //let result = { 'packet': selectedProducts, 'modelDialog': false }
        let result = { 'packet': packet, 'modelDialog': false }
        this.submitEvent.emit(result);
      });
  }

  prepareProductsOffers(packet: any): ProductOfferDTO[] {
    let productsOffers: ProductOfferDTO[] = [];
    this.packetDescription = '';
    this.stockAvailable= 200;
    this.colorSizeChoosen =true;
    for (var i = 0; i < packet.offers.length; i++) {
      let offer = packet.offers[i];
      if (offer.offerId != null && offer.offerId != undefined) {
        if (offer.models.length > 0) {
          for (var j = 0; j < offer.models.length; j++) {
            let qte =offer.models[j].selectedProduct.quantity;

            let colorSizeFalse = offer.models[j].selectedProduct.size.reference == "?" || offer.models[j].selectedProduct.color.name == "?";
            let x =colorSizeFalse? -1 : qte < 1 ? 0 : qte;
            this.stockAvailable = x < this.stockAvailable ? x:this.stockAvailable;

            productsOffers.push({ productId: offer.models[j].selectedProduct.id, offerId: offer.offerId, packetOfferIndex: i });
            this.setPacketDescription(offer.models[j]?.name,
              this.getElement(offer.models[j], 'selectedColor', 'name'),
              this.getElement(offer.models[j], 'selectedSize', 'reference'),
              this.getElement(offer.models[j], 'selectedSizeReel', 'reference'));
          }
        }
      }
    }

    this.packetDescription = StringUtils.removeChars(this.packetDescription, 2);
    return productsOffers;
  }

  createPacketDescription() {
    this.packetDescription = '';
    this.productReferences = '';
    let packet = this.packetForm.value;
    for (var i = 0; i < packet.offers.length; i++) {
      let offer = packet.offers[i];
      if (offer.offerId != null && offer.offerId != undefined) {
        if (offer.models.length > 0) {
          for (var j = 0; j < offer.models.length; j++) {
            this.setPacketDescription(offer.models[j]?.name,
              this.getElement(offer.models[j], 'selectedColor', 'name'),
              this.getElement(offer.models[j], 'selectedSize', 'reference'),
              this.getElement(offer.models[j], 'selectedSizeReel', 'reference'));
          }
        }
      }
    }
    this.packetDescription = StringUtils.removeChars(this.packetDescription, 2);
    this.productReferences = StringUtils.removeChars(this.productReferences, 2);
  }

  getElement(model: any, field: string, field2: string) {
    return model[field] != null ? model[field][field2] : null;
  }

  setPacketDescription(modelName?: any, color?: any, size?: any, fakeSize?:any) {
    //console.log('fakeSize',fakeSize);
    if (modelName != null)
      this.packetDescription += modelName;
    if (color != null && color != "?")
      this.packetDescription += ' ' + color;
    if (fakeSize != null)
      this.packetDescription += ' (' + fakeSize + ')';
    else if (size != null && size != "?")
      this.packetDescription += ' (' + size + ')';
    this.packetDescription += ' , ';
  }

  calculateTotalPrice() {
    this.totalPrice = 0;
    for (var i = 0; i < this.offers().length; i++) {
      this.totalPrice += this.offers().at(i).get('price')?.value;
    }
    this.packetForm.controls['totalPrice'].setValue(this.totalPrice);
  }

  calculatePacketPrice() {
    this.packetPrice = this.totalPrice + this.packetForm.controls['deliveryPrice'].value - this.packetForm.controls['discount'].value;
  }

  enableFake(){
    this.enableFakeSize = !this.enableFakeSize
  }

/*   getStock(model: any,offer:any) {
    console.log("model",model);
    console.log("offer",offer);


    if(model.selectedProduct?.quantity<1 && this.stockDisponible)
    {this.stockDisponible=false;
    this.enableFakeSize = true}
    console.log(this.stockDisponible);
    return model.selectedProduct?.quantity;
  } */
}
