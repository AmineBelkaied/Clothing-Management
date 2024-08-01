import { ChangeDetectorRef, Component, EventEmitter, Input, model, OnInit, Output } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { PacketService } from '../../../shared/services/packet.service';
import { ProductsPacket } from 'src/shared/models/ProductsPacket';
import { Color } from 'src/shared/models/Color';
import { Size } from 'src/shared/models/Size';
import { ConfirmationService, MessageService } from 'primeng/api';
import { OOS, NOT_CONFIRMED } from 'src/shared/utils/status-list';
import { Offer } from 'src/shared/models/Offer';
import { Packet } from 'src/shared/models/Packet';
import { OfferService } from 'src/shared/services/offer.service';
import { map, Subject, takeUntil, tap } from 'rxjs';
import { Product } from 'src/shared/models/Product';
import { ProductService } from 'src/shared/services/product.service';

@Component({
  selector: 'app-add-packet',
  templateUrl: './add-packet.component.html',
  styleUrls: ['./add-packet.component.css'],
  providers: [ConfirmationService, MessageService]
})
export class AddPacketComponent implements OnInit {

  @Input() packet: any;
  @Input() offersIdsListByFbPage: any[] = [];
  @Input() editMode: boolean = false;
  @Input() modelDialog: boolean = false;

  @Output() submitEvent: EventEmitter<any> = new EventEmitter();

  allOffersList: any[] = [];
  enableFakeSize : boolean = false;
  enableAllOffer : boolean = false;
  productsPrice: number = 0;
  packetDescription: string = '';
  productReferences: string = '';
  packetForm: FormGroup;
  packetPrice: number = 0;
  selectedSizeReel : string = '';
  noChoiceColor!: Color;
  noChoiceSize!: Size;
  stockAvailable: number;
  colorSizeChoosen: boolean = true;
  productCount: number;
  packetGainCoefficient =0;
  packetPurshasePrice = 0;
  packetEarningCoefficient=0;
  allProducts: Product[];
  $unsubscribe: Subject<void> = new Subject();
  offersSelected: Offer[]
  allOffersListEnabled: Offer[];

  constructor(
    private fb: FormBuilder,
    private cdRef: ChangeDetectorRef,
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private offersService: OfferService,
    private productService: ProductService
  ) {
    this.packetForm = this.fb.group({
      totalPrice: 0,
      deliveryPrice: 7,
      discount: 0,
      offers: this.fb.array([])
    });
  }

  ngOnInit(): void {
    this.productService.productsSubscriber.pipe(takeUntil(this.$unsubscribe)).subscribe((products:Product[]) => this.allProducts = products)

    this.offersService.getOffersSubscriber()
      .pipe(
        takeUntil(this.$unsubscribe),
        tap((offersList: Offer[]) => {
          if(offersList){
            this.allOffersList = offersList;
            console.log(this.allOffersList);
            this.allOffersListEnabled = offersList.filter((offer: Offer) => offer.enabled);
            this.offersIdsListByFbPage = this.allOffersListEnabled.filter((offer: Offer) =>
              offer.fbPages.map(fbpage => fbpage.id).includes(this.packet.fbPage.id)
            );
            console.log("offersIdsListByFbPage", this.offersIdsListByFbPage);
            this.getOffers();
            this.editMode ? this.getSelectedProducts() : this.addOffer();
            this.stockAvailable = 1;
          }
        })
      ).subscribe();

  }


  getSelectedProducts(): void {
    console.log("getSelectedProducts");

    this.packetService.findPacketRelatedProducts(this.packet.id)
      .subscribe((offers: any) => {
        for (var index = 0; index < offers.length; index++) {
          let products : Product[] =offers[index].products;
          console.log("allOffersList",this.allOffersList);
          console.log("offers",offers);
          let offer : Offer = this.allOffersList.find(offer => offer.id == offers[index].id);
          console.log("offer",offer);

          this.addSelectedOffer(offer);
          this.addSelectedModels(products, index);
        }
        this.calculateProductsPrice();
        this.calculatePacketPrice();
        this.packetForm.controls['deliveryPrice'].setValue(this.packet.deliveryPrice);
        this.packetForm.controls['discount'].setValue(this.packet.discount);
      });
  }



// start control block
  setOfferControlValues(offerControl: AbstractControl, offerValue: Offer): void {
    this.setControlValue(offerControl, 'offerId', offerValue.id);
    this.setControlValue(offerControl, 'name', offerValue.name);
    this.setControlValue(offerControl, 'price', offerValue.price);
  }

  setModelControlValues(modelControl: AbstractControl,selectedModel:Model,selectedProduct?:Product): void {

    this.setControlValue(modelControl, 'name', selectedModel.name);

    this.noChoiceColor = selectedModel.colors.find((color: Color) => color.reference === "?")!;
    let colors : Color[] = selectedModel.colors.filter((color: Color) => color.reference != "?");
    this.setControlValue(modelControl, 'colors', colors);

    this.noChoiceSize = selectedModel.sizes.find((size: Size) => size.reference === "?")!;
    let sizes : Size[] = selectedModel.sizes.filter((size: Size) => size.reference != "?");
    this.setControlValue(modelControl, 'sizes', sizes);

    if(selectedProduct)
      this.setProductControlValues(modelControl, selectedProduct);
    else{
      let defaultProduct = this.allProducts?.find((product: any) => product.color.reference === "?" && product.size.reference === "?" );
      this.setControlValue(modelControl, 'selectedProduct', defaultProduct);
    }
  }

  setProductControlValues(productControl: AbstractControl,selectedProduct:Product): void {
    this.setControlValue(productControl, 'selectedProduct', selectedProduct);
    this.setControlValue(productControl, 'selectedColor', selectedProduct.color);
    this.setControlValue(productControl, 'selectedSize', selectedProduct.size);
    this.setControlValue(productControl, 'qte', selectedProduct.qte);
  }
  removeOffer(offer: any, i: number): void {
    this.offers().removeAt(i);
    this.calculatePacketPrice();
    this.createPacketDescription();
  }
  // end control block

  //start set data block
  addSelectedModels(products: Product[], offerIndex: number): void {
    if (products != null && products.length > 0)
      for (var j = 0; j < products.length; j++) {
        console.log("products[j]",products[j]);
        if (products[j].id != null && products[j].model?.id != null) {
          let selectedModel : Model = products[j].model!;
          console.log("this.allProducts",this.allProducts);

          let selectedProduct : Product = this.allProducts.find((product: Product) => product.id == products[j].id)!;
          if(selectedProduct){
            this.pushModelToOffer(offerIndex,j,selectedModel,selectedProduct);
          }else console.log("selectedProduct",products[j].id+" =null");
        }else console.log("product",products[j].id+" introuvable");
      }else console.log("products est vide");

  }

  setOfferModelsValues(offerIndex: number,offer:Offer): void {
    this.setOfferControlValues(this.offers().at(offerIndex), offer);
    for (var i = 0; i < offer.offerModels.length; i++) {
      let selectedModel : Model = offer.offerModels[i].model;
      this.pushModelToOffer(offerIndex,i,selectedModel);
    }
  }
  pushModelToOffer(offerIndex: number,modelIndex:number,model:Model,selectedProduct?:Product){
    console.log("selectedProduct",selectedProduct);

    this.addModel(offerIndex);
    this.setModelControlValues(this.models(offerIndex).controls[modelIndex],model,selectedProduct);
    this.addProductToPacketDescription(selectedProduct?.model?.name, selectedProduct?.color.name, selectedProduct?.size.reference);
  }

  onOfferChange(offerId: number, index: number): void {
    this.models(index).clear();
    let offer : Offer = this.allOffersList.find(offer => (offer.id === offerId))!;
    if(offer){
      if (offer != null && offer.offerModels.length > 0) {
        this.setOfferModelsValues(index,offer);
      }
      this.calculateProductsPrice();
      this.calculatePacketPrice();
    }
  }

  colorSizeChange(selectedModel: AbstractControl, modelIndex: number, selectedOffer: AbstractControl): void {
      let offer : Offer = this.allOffersList.find(offer => offer.name == selectedOffer.get('name')?.value);
      console.log("Offer0",offer);
      if(offer){
        console.log("Offer1",offer);
        this.setNoChoiceColorSize(selectedModel, modelIndex,offer);
        let model : Model =offer.offerModels[modelIndex].model;
        console.log("model",model);
        let color : Color = selectedModel.get('selectedColor')?.value
        let size : Size = selectedModel.get('selectedSize')?.value;
        let selectedProduct : Product = this.allProducts.find(
          (product: Product) =>
            product.modelId == model.id
            && product.color.id == color.id
            && product.size.id == size.id
          )!;
        if(selectedProduct){
          console.log("selectedProduct",selectedProduct);
          selectedModel.get('selectedProduct')?.setValue(selectedProduct);
          this.createPacketDescription();
        }
      }
  }
  setNoChoiceColorSize(selectedModel: AbstractControl, index: number,offer:Offer): void {
    if (!selectedModel.get('selectedColor')?.value) {
      let noChoiceColor : Color = offer.offerModels[index].model.colors.find((color: Color) => color.reference == "?");
      selectedModel.get('selectedColor')?.setValue(noChoiceColor);
    }
    if (!selectedModel.get('selectedSize')?.value) {
      let noChoiceSize : Size = offer.offerModels[index].model.sizes.find((size: Size) => size.reference == "?");
      selectedModel.get('selectedSize')?.setValue(noChoiceSize);
    }
  }
  //end set data block
// start submit block
  onSubmit(event:Event) {
    let productsOffers: ProductsPacket[] = this.prepareProductsOffers();
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

  submitProductsOffers(productsOffers: ProductsPacket[], stock:number){
    console.log("start submit");
    let selectedProducts ={};
    if(stock)
      selectedProducts = { 'idPacket': this.packet.id, 'totalPrice': this.productsPrice, 'productsOffers': productsOffers, 'packetDescription': this.packetDescription, 'deliveryPrice': this.packetForm.value.deliveryPrice, 'discount': this.packetForm.value.discount,'status': NOT_CONFIRMED, 'productCount':this.productCount};
    else
      selectedProducts = { 'idPacket': this.packet.id, 'totalPrice': this.productsPrice, 'productsOffers': productsOffers, 'packetDescription': this.packetDescription, 'deliveryPrice': this.packetForm.value.deliveryPrice, 'discount': this.packetForm.value.discount,'status': OOS, 'productCount':this.productCount };

    this.packetService.addProductsToPacket(selectedProducts)
      .subscribe((packet: any) => {
        console.log("end submit");
        let result = { 'packet': packet, 'modelDialog': false }
        this.submitEvent.emit(result);

      });
  }

  prepareProductsOffers(): ProductsPacket[] {
    console.log("start prepare");
    let productsOffers: ProductsPacket[] = [];
    this.packetDescription = '';
    this.stockAvailable= 200;
    this.colorSizeChoosen =true;
    this.productCount = 0;
    this.packetGainCoefficient =0;
    this.packetPurshasePrice = 0;
    let packet : Packet = this.packetForm.value;// correction type
    //calcul gain total du packet + coefficient de gain total
    for (var i = 0; i < packet.offers.length; i++) {
      let offer :Offer= packet.offers[i];
           if (offer.id != null && offer.id != undefined) {
        if (offer.offerModels!= null && offer.offerModels.length > 0) {
          for (var j = 0; j < offer.offerModels.length; j++) {
            let model : Model = offer.offerModels[j];
            this.packetGainCoefficient += model.earningCoefficient!;
            this.packetPurshasePrice += model.purchasePrice!;
          }
        }
      }
    }
    for (var i = 0; i < packet.offers.length; i++) {
      let offer : Offer = packet.offers[i];
      console.log("offer-"+i+":",offer);
      if (offer.id != null && offer.id != undefined) {
        if (offer.offerModels!= null && offer.offerModels.length > 0) {
          for (var j = 0; j < offer.offerModels.length; j++) {
            let model : Model = offer.offerModels[j];
            console.log("model-"+j+":",model);
            let selectedProduct = model.selectedProduct;
            if(selectedProduct !== undefined){
              let qte = selectedProduct.qte;
              let colorSizeFalse : boolean = offer.offerModels[j].selectedSize?.reference == "?" || offer.offerModels[j].selectedColor?.name == "?";
              let x : number = colorSizeFalse ? -1 : qte < 1 ? 0 : qte ?? 0;
              this.stockAvailable = (x < this.stockAvailable) ? x : this.stockAvailable;
              this.productCount+=1;
              this.packetEarningCoefficient = (packet.price-packet.discount-this.packetPurshasePrice)/this.packetGainCoefficient;

              productsOffers.push({ productId: selectedProduct.id , offerId: offer.id, packetOfferIndex: i,
                profits: this.packetEarningCoefficient*model.earningCoefficient});
              this.addProductToPacketDescription(
                model.name,
                this.getElement(offer.offerModels[j], 'selectedColor', 'name'),
                this.getElement(offer.offerModels[j], 'selectedSize', 'reference'),
                this.getElement(offer.offerModels[j], 'selectedSizeReel', 'reference'));
            }

          }
        }
      }
    }
    console.log("end prepare");
    return productsOffers;
  }
// end submit block
  createPacketDescription() {
    this.packetDescription = '';
    let packet = this.packetForm.value;
    for (var i = 0; i < packet.offers.length; i++) {
      let offer : Offer= packet.offers[i];
      console.log("offer",offer);

      if (offer.id != null && offer.id != undefined) {

        if (offer.offerModels!=null && offer.offerModels.length > 0) {
          for (var j = 0; j < offer.offerModels.length; j++) {
            let model:Model = offer.offerModels[j];
            this.addProductToPacketDescription(
              offer.offerModels[j].name,
              this.getElement(model, 'selectedColor', 'name'),
              this.getElement(model, 'selectedSize', 'reference'),
              this.getElement(model, 'selectedSizeReel', 'reference'));
          }
        }
      }
    }
    console.log("createPacketDescription",this.packetDescription);
  }

  getOffers(){
    this.offersSelected = this.enableAllOffer?this.allOffersListEnabled:this.offersIdsListByFbPage;
  }

  ngAfterViewChecked(): void {
    this.cdRef.detectChanges();
  }

  setControlValue(control: AbstractControl, controlName: string, controlValue: any): void {
    control.get(controlName)?.setValue(controlValue);
  }

  offers(): FormArray {
    return this.packetForm.get('offers') as FormArray
  }

  models(offerIndex: number): FormArray {
    return this.offers().at(offerIndex).get('offerModels') as FormArray
  }

  products(modelIndex: number, productIndex: number): FormArray {
    return this.models(modelIndex).at(productIndex).get('products') as FormArray
  }

  addSelectedOffer(offer: Offer): void {
    this.offers().push(
      this.fb.group({
        id: offer.id,
        name: offer.name,
        price: offer.price,
        offerModels: this.fb.array([])
      })
    );
  }

  newOffer(): FormGroup {
    return this.fb.group({
      id: 0,
      name: '',
      price: 0,
      offerModels: this.fb.array([])
    })
  }

  newModel(): FormGroup {
    let model = this.fb.group({
      name: '',
      colors: [],
      sizes: [],
      purchasePrice:15,
      earningCoefficient:1,
      selectedColor: '',
      selectedSize: '',
      selectedProduct: '',
      selectedSizeReel:''
    })
    return model;
  }

  getElement(model: any, field: string, field2: string) {
    return model[field] != null ? model[field][field2] : null;
  }

  addOffer(): void {
    this.offers().push(this.newOffer());
  }

  addModel(modelIndex: number): void {
    this.models(modelIndex).push(this.newModel());
  }

  addProductToPacketDescription(modelName?: any, color?: any, size?: any, fakeSize?:any) {
    //console.log('fakeSize',fakeSize);
    if (this.packetDescription.length>0)
      this.packetDescription += ' , ';
    if (modelName != null)
      this.packetDescription += modelName;
    if (color != null && color != "?")
      this.packetDescription += ' ' + color;
    if (fakeSize != null)
      this.packetDescription += ' (' + fakeSize + ')';
    else if (size != null && size != "?")
      this.packetDescription += ' (' + size + ')';
    console.log("addProductToPacketDescription",this.packetDescription);

  }

  calculateProductsPrice() {
    this.productsPrice = 0;
    for (var i = 0; i < this.offers().length; i++) {
      this.productsPrice += this.offers().at(i).get('price')?.value;
    }
  }

  calculatePacketPrice() {
    this.packetPrice = this.productsPrice + this.packetForm.controls['deliveryPrice'].value - this.packetForm.controls['discount'].value;
  }

  enableFake(){
    this.enableFakeSize = !this.enableFakeSize
  }


  ngOnDestroy(): void {
    console.log("$unsubscribe");

    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
  /*clearModel(offerName: any, index: number): void {
    this.models(index).clear();
    let offer: Offer = this.offersSelected.find(off => off.name == offerName)!;
    if (offer != null && offer.offerModels.length > 0) {
      this.setOfferControlValues(this.offers().at(index), offer);
      for (var i = 0; i < offer.offerModels.length; i++) {
          this.addModel(index);
          this.setModelControlValues(this.models(index).controls[i],offer.offerModels[i].model);
        }
      }
  }*/
