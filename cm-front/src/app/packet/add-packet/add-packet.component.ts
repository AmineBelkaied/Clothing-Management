import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { PacketService } from '../../../shared/services/packet.service';
import { ProductOfferDTO } from 'src/shared/models/ProductOfferDTO';
import { StringUtils } from 'src/shared/utils/string-utils';
import { Color } from 'src/shared/models/Color';
import { Size } from 'src/shared/models/Size';

@Component({
  selector: 'app-add-packet',
  templateUrl: './add-packet.component.html',
  styleUrls: ['./add-packet.component.css']
})
export class AddPacketComponent implements OnInit {

  @Input() packet: any;

  @Input() offersList: any[] = [];

  @Input() editMode: boolean = false;

  @Input() modelDialog: boolean = false;

  @Output() submitEvent: EventEmitter<any> = new EventEmitter();

  private selectedOffer: any;
  totalPrice: number = 0;
  packetDescription: string = '';
  productReferences: string = '';
  packetForm: FormGroup;
  packetPrice: number = 0;
  noChoiceColor!: Color;
  noChoiceSize!: Size;
  constructor(private fb: FormBuilder, private cdRef: ChangeDetectorRef, private packetService: PacketService) {
    this.packetForm = this.fb.group({
      totalPrice: 0,
      deliveryPrice: 7,
      discount: 0,
      offers: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.editMode ? this.getSelectedProducts() : this.addOffer();
  }

  ngAfterViewChecked(): void {
    this.cdRef.detectChanges();
  }

  offers(): FormArray {
    return this.packetForm.get('offers') as FormArray
  }

  models(offerIndex: number): FormArray {
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
      selectedProduct: ''
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
          this.setPacketDescription(model.name, offer.products[j].color.name, offer.products[j].size.reference);
          this.productReferences += this.createProductRef(model.reference, offer.products[j].color.reference, offer.products[j].size.reference).concat(' , ');
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
    this.noChoiceColor = modelValue.colors.find((color: Color) => color.reference === "?");
    let colors = modelValue.colors.filter((color: Color) => color.reference != "?");
    this.setControlValue(modelControl, 'colors', colors);

    this.noChoiceSize = modelValue.sizes.find((size: Size) => size.reference === "?");
    let sizes = modelValue.sizes.filter((size: Size) => size.reference != "?");
    this.setControlValue(modelControl, 'sizes', sizes);

    this.setControlValue(modelControl, 'name', modelValue.name);
    this.setControlValue(modelControl, 'reference', modelValue.reference);

    let defaultProduct = modelValue.products?.find((product: any) => product.reference === modelValue.reference + "??");
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

  onOfferChange(offerName: string, index: number): void {
    this.models(index).clear();
    this.selectedOffer = this.offersList.find(off => off.name === offerName);
    if (this.selectedOffer != null && this.selectedOffer.models.length > 0) {
      this.setOfferModelsValues(index);
    }
    this.calculateTotalPrice();
    this.calculatePacketPrice();
  }

  setOfferModelsValues(index: number): void {
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
      this.selectedOffer = this.offersList.find(offer => offer.offerId == selectedOffer.get('offerId')?.value)
    
    this.setNoChoiceColorSize(selectedModel, index);
    let selectedProduct = this.selectedOffer.models[index].products.find((product: any) => product.color.id == selectedModel.get('selectedColor')?.value.id && product.size.id == selectedModel.get('selectedSize')?.value.id);
    selectedModel.get('selectedProduct')?.setValue(selectedProduct);
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


  createProductRef(modelRef: any, colorRef: any, sizeRef: any) {
    let ref = (modelRef != null && modelRef != '') ? modelRef : '?';
    let colorModel = (colorRef != null && colorRef != '') ? colorRef : '?';
    let sizeModel = (sizeRef != null && sizeRef != '') ? sizeRef : '?';
    let productRef = ref.concat(colorModel).concat(sizeModel);
    return productRef;
  }

  onSubmit() {
    let productsOffers: ProductOfferDTO[] = this.prepareProductsOffers(this.packetForm.value);
    let selectedProducts = { 'idPacket': this.packet.id, 'totalPrice': this.totalPrice, 'productsOffers': productsOffers, 'packetDescription': this.packetDescription, 'deliveryPrice': this.packetForm.value.deliveryPrice, 'discount': this.packetForm.value.discount };
    this.packetService.addProductsToPacket(selectedProducts)
      .subscribe(() => {
        let result = { 'packet': selectedProducts, 'modelDialog': false }
        this.submitEvent.emit(result);
      });
  }

  prepareProductsOffers(packet: any): ProductOfferDTO[] {
    let productsOffers: ProductOfferDTO[] = [];
    this.packetDescription = '';
    for (var i = 0; i < packet.offers.length; i++) {
      let offer = packet.offers[i];
      if (offer.offerId != null && offer.offerId != undefined) {
        if (offer.models.length > 0) {
          for (var j = 0; j < offer.models.length; j++) {
            productsOffers.push({ productId: offer.models[j].selectedProduct.id, offerId: offer.offerId, packetOfferIndex: i });
            this.setPacketDescription(offer.models[j]?.name,
              this.getElement(offer.models[j], 'selectedColor', 'name'),
              this.getElement(offer.models[j], 'selectedSize', 'reference'));
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
            this.productReferences += this.createProductRef(offer.models[j].reference,
              this.getElement(offer.models[j], 'selectedColor', 'reference'),
              this.getElement(offer.models[j], 'selectedSize', 'reference')).concat(' , ');

            this.setPacketDescription(offer.models[j]?.name,
              this.getElement(offer.models[j], 'selectedColor', 'name'),
              this.getElement(offer.models[j], 'selectedSize', 'reference'));
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

  setPacketDescription(modelName?: any, color?: any, size?: any) {
    if (modelName != null)
      this.packetDescription += modelName;
    if (color != null && color != "?")
      this.packetDescription += ' ' + color;
    if (size != null && size != "?")
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
}
