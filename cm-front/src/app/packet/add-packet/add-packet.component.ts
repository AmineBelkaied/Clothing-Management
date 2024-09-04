import {
  ChangeDetectorRef,
  Component,
  EventEmitter,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  FormGroup,
} from '@angular/forms';
import { Model } from 'src/shared/models/Model';
import { PacketService } from '../../../shared/services/packet.service';
import { ProductsPacket } from 'src/shared/models/ProductsPacket';
import { Color } from 'src/shared/models/Color';
import { Size } from 'src/shared/models/Size';
import { ConfirmationService, MessageService } from 'primeng/api';
import { OOS, NOT_CONFIRMED } from 'src/shared/utils/status-list';
import { Offer } from 'src/shared/models/Offer';
import { OfferService } from 'src/shared/services/offer.service';
import { Subject, takeUntil, tap } from 'rxjs';
import { Product } from 'src/shared/models/Product';
import { ProductService } from 'src/shared/services/product.service';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-add-packet',
  templateUrl: './add-packet.component.html',
  styleUrls: ['./add-packet.component.css'],
  providers: [ConfirmationService, MessageService],
})
export class AddPacketComponent implements OnInit {
  @Input() packet: any;
  @Input() offersIdsListByFbPage: any[] = [];
  @Input() editMode: boolean = false;
  @Input() modelDialog: boolean = false;

  @Output() submitEvent: EventEmitter<any> = new EventEmitter();

  allOffersList: any[] = [];
  enableFakeSize: boolean = false;
  enableAllOffer: boolean = false;
  productsPrice: number = 0;
  packetDescription: string = '';
  productReferences: string = '';
  packetForm: FormGroup;
  packetPrice: number = 0;
  selectedSizeReel: string = '';
  //noChoiceColor!: Color;
  //noChoiceSize!: Size;
  stockAvailable: number;
  colorSizeChoosen: boolean = true;
  productCount: number;
  packetEarningCoefficient = 0;
  allProducts: Product[];
  $unsubscribe: Subject<void> = new Subject();
  offersSelected: Offer[];
  allOffersListEnabled: Offer[];

  constructor(
    private fb: FormBuilder,
    private cdRef: ChangeDetectorRef,
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private offersService: OfferService,
    private productService: ProductService,
    private decimalPipe: DecimalPipe
  ) {
    this.packetForm = this.fb.group({
      totalPrice: 0,
      deliveryPrice: 7,
      discount: 0,
      offers: this.fb.array([]),
    });
  }

  ngOnInit(): void {
    this.productService.productsSubscriber
      .pipe(takeUntil(this.$unsubscribe))
      .subscribe((products: Product[]) => (this.allProducts = products));

    this.offersService
      .getOffersSubscriber()
      .pipe(
        takeUntil(this.$unsubscribe),
        tap((offersList: Offer[]) => {
          if (offersList) {
            this.allOffersList = offersList;

            this.allOffersListEnabled = offersList.filter(
              (offer: Offer) => offer.enabled
            );
            this.offersIdsListByFbPage = this.allOffersListEnabled.filter(
              (offer: Offer) =>
                offer.fbPages
                  .map((fbpage) => fbpage.id)
                  .includes(this.packet.fbPage.id)
            );
            this.getOffersSwitch();
            this.editMode ? this.getSelectedProducts() : this.addOffer();
          }
        })
      )
      .subscribe();
  }

  // edit mode after init
  getSelectedProducts(): void {
    this.productsPrice = 0;
    this.packetService
      .findPacketRelatedProducts(this.packet.id)
      .subscribe((offers: any) => {
        offers.forEach((offer: any) => {
          let index = offer.packetOfferId;
          let products: Product[] = offer.products;
          let offerX: Offer = this.allOffersList.find(
            (offerW) => offerW.id == offer.id
          );
          //console.log("offerX",offerX);
          this.addSelectedOffer(offerX);
          this.addSelectedModels(products, index);
        });
        this.packetForm.controls['deliveryPrice'].setValue(
          this.packet.deliveryPrice
        );
        this.packetForm.controls['discount'].setValue(this.packet.discount);
        this.calculateProductsPrice();
        //this.calculatePacketPrice();
      });
  }

  // start control block
  setOfferControlValues(
    offerControl: AbstractControl,
    offerValue: Offer
  ): void {
    this.setControlValue(offerControl, 'offerId', offerValue.id);
    this.setControlValue(offerControl, 'name', offerValue.name);
    this.setControlValue(offerControl, 'price', offerValue.price);
  }

  setControlValue(
    control: AbstractControl,
    controlName: string,
    controlValue: any
  ): void {
    control.get(controlName)?.setValue(controlValue);
  }

  removeOffer(offer: any, i: number): void {
    this.offers().removeAt(i);
    this.calculateProductsPrice();
  }
  // end control block

  //start set data block
  // edit mode after offer add (2)
  addSelectedModels(products: Product[], offerIndex: number): void {
    if (products.length > 0)
      for (let j = 0; j < products.length; j++) {
        console.log('products['+j+']', products[j]);
        if (products[j].id != null && products[j].model?.id != null) {
          let selectedModel: Model = products[j].model!;
         /* let selectedProduct: Product = this.allProducts.find(
            (product: Product) => product.id == products[j].id
          )!;
          if (selectedProduct) {*/
            this.pushModelToOffer(offerIndex,selectedModel,products[j]);
          //} else console.log('selectedProduct', products[j].id + ' =null');
        } else console.log('product', products[j].id + ' introuvable');
      }
    else console.log('products est vide');
  }

  setOfferModelsValues(offerIndex: number, offer: Offer): void {
    this.setOfferControlValues(this.offers().at(offerIndex), offer);
    let pos = 0;
    for (let i = 0; i < offer.offerModels.length; i++) {
      let selectedModel: Model = offer.offerModels[i].model;
      for (let j = 0; j < offer.offerModels[i].quantity; j++) {
        let selectedProduct: Product = this.getNoChoiceColorSizeProduct(selectedModel);
        this.pushModelToOffer(offerIndex, selectedModel, selectedProduct);
        pos++;
      }
    }
  }

  pushModelToOffer(
    offerIndex: number,
    model: Model,
    selectedProduct: Product
  ) {
    this.addModel(offerIndex,model,selectedProduct);
  }

  onOfferChange(offerId: number, index: number): void {
    this.models(index).clear();
    let offer: Offer = this.allOffersList.find(
      (offer) => offer.id === offerId
    )!;
    if (offer) {
      if (offer.offerModels.length > 0) {
        this.setOfferModelsValues(index, offer);
      }
      this.calculateProductsPrice();
      //this.calculatePacketPrice();
    }
  }

  colorSizeChange(
    selectedModel: AbstractControl,
    modelIndex: number,
    selectedOffer: AbstractControl
  ): void {
    let offer: Offer = this.allOffersList.find(
      (offer) => offer.name == selectedOffer.get('name')?.value
    );
    console.log('Offer0', offer);
    if (offer) {
      console.log('Offer1', offer);
      this.setNoChoiceColorSize(selectedModel, modelIndex, offer);
      console.log('modelIndex', modelIndex);

      let pos = 0;

      let model: Model | undefined;
      for (let i = 0; i < offer.offerModels.length; i++) {
        for (let j = 0; j < offer.offerModels[i].quantity; j++) {
          if (pos === modelIndex) {
            // Use '===' for comparison
            console.log('offer.offerModels[i]', offer.offerModels[i]);
            model = offer.offerModels[i].model;
            break; // Exit the loop once the model is found
          }
          pos++;
        }
        if (model) break; // Exit the outer loop if model is found
      }
      console.log('model', model);
      console.log('selectedModel', selectedModel);

      let color: Color = selectedModel.get('selectedColor')?.value;
      let size: Size = selectedModel.get('selectedSize')?.value;
      let selectedProduct: Product = this.allProducts.find(
        (product: Product) =>
          product.modelId == model?.id &&
          product.color.id == color.id &&
          product.size.id == size.id
      )!;
      if (selectedProduct) {
        console.log('selectedProduct', selectedProduct);
        selectedModel.get('selectedProduct')?.setValue(selectedProduct);
      }
    }
  }
  getNoChoiceColorSizeProduct(model: Model): Product {
    console.log('model', model);
    return this.allProducts.find(
      (product: Product) =>
        product.color.name == '?' &&
        product.size.reference == '?' &&
        model.id == product.modelId
    )!;
  }
  setNoChoiceColorSize(
    selectedModel: AbstractControl,
    index: number,
    offer: Offer
  ): void {
    console.log('selectedModel', selectedModel);

    if (!selectedModel.get('selectedColor')?.value) {
      let noChoiceColor: Color = offer.offerModels[index].model.colors.find(
        (color: Color) => color.name == '?'
      );
      selectedModel.get('selectedColor')?.setValue(noChoiceColor);
    }
    if (!selectedModel.get('selectedSize')?.value) {
      let noChoiceSize: Size = offer.offerModels[index].model.sizes.find(
        (size: Size) => size.reference == '?'
      );
      selectedModel.get('selectedSize')?.setValue(noChoiceSize);
    }
  }
  //end set data

  //start submit block
  onSubmit(event: Event) {
    let packetGainCoefficient = 0;
    let packetPurshasePrice = 0;
    let packetEarningCoefficient: number;
    let packet = this.packetForm.value;
    for (let i = 0; i < packet.offers.length; i++) {
      let offer: Offer = packet.offers[i];
      if (offer.id != null) {
        if (offer.offerModels.length > 0) {
          offer.offerModels.forEach((model: Model) => {
            if (model) {
              packetGainCoefficient += model.earningCoefficient;
              packetPurshasePrice += model.purchasePrice;
            }
          });
        }
      }
    }
    if (packetGainCoefficient === 0) {
      console.error(
        'packetGainCoefficient is zero, cannot calculate packetEarningCoefficient'
      );
    }
    let gain = this.productsPrice - packet.discount - packetPurshasePrice;
    packetEarningCoefficient = gain / packetGainCoefficient;
    let productsOffers: ProductsPacket[] = this.prepareProductsOffers(
      packet,
      packetEarningCoefficient
    );
    if (this.stockAvailable < 1) {
      this.enableFakeSize = true;
      this.confirmationService.confirm({
        target: event.target as EventTarget,
        message:
          this.stockAvailable < 0
            ? 'Veiller remplir tous les champs'
            : 'Stock 0-Veiller le remplacer par un fake size.',
        icon: 'pi pi-exclamation-circle',
        acceptIcon: 'pi pi-check mr-1',
        rejectIcon: 'pi pi-times mr-1',
        rejectButtonStyleClass: 'p-button-danger p-button-sm',
        acceptButtonStyleClass: 'p-button-outlined p-button-sm',
        accept: () => {
          this.messageService.add({
            severity: 'info',
            summary: 'Confirmed',
            detail: 'Veiller remplir fake size',
            life: 3000,
          });
        },
        reject: () => {
          this.submitProductsOffers(productsOffers, this.stockAvailable);
          this.messageService.add({
            severity: 'error',
            summary: 'Rejected',
            detail: 'You have rejected',
            life: 3000,
          });
        },
      });
    } else {
      this.submitProductsOffers(productsOffers, this.stockAvailable);
    }
  }

  submitProductsOffers(productsOffers: ProductsPacket[], stock: number) {
    console.log('start submit');
    let selectedProducts: {};
    let status = OOS;
    if (stock) status = NOT_CONFIRMED;
    selectedProducts = {
      idPacket: this.packet.id,
      totalPrice: this.productsPrice,
      productsOffers: productsOffers,
      packetDescription: this.packetDescription,
      deliveryPrice: this.packetForm.value.deliveryPrice,
      discount: this.packetForm.value.discount,
      status: status,
      productCount: this.productCount,
    };
    console.log('selectedProducts', selectedProducts);
    this.packetService
      .addProductsToPacket(selectedProducts)
      .subscribe((packet: any) => {
        console.log('end submit');
        let result = { packet: packet, modelDialog: false };
        this.submitEvent.emit(result);
      });
  }

  prepareProductsOffers(
    packet: any,
    packetEarningCoefficient: number
  ): ProductsPacket[] {
    console.log('start prepare');
    let productsOffers: ProductsPacket[] = [];
    this.packetDescription = '';
    this.stockAvailable = 200;
    this.colorSizeChoosen = true;
    this.productCount = 0;

    for (let i = 0; i < packet.offers.length; i++) {
      let offer: Offer = packet.offers[i];
      console.log('offer-' + i + ':', offer);
      if (offer.id != null) {
        if (offer.offerModels.length > 0) {
          for (let j = 0; j < offer.offerModels.length; j++) {
            let model: Model = offer.offerModels[j];
            //create packet description
            this.addProductToPacketDescription(
              offer.offerModels[j].name,
              this.getElement(model, 'selectedColor', 'name'),
              this.getElement(model, 'selectedSize', 'reference'),
              this.getElement(model, 'selectedSizeReel', 'reference')
            );

            console.log('model-' + j + ':', model);
            let selectedProduct = model.selectedProduct;
            if (selectedProduct !== undefined) {
              let qte = selectedProduct.qte;
              let colorSizeFalse: boolean =
                offer.offerModels[j].selectedSize?.reference == '?' ||
                offer.offerModels[j].selectedColor?.name == '?';
              let x: number = colorSizeFalse ? -1 : qte < 1 ? 0 : qte ?? 0;
              this.stockAvailable = x < this.stockAvailable ? x : this.stockAvailable;
              this.productCount += 1;

              const profits = packetEarningCoefficient * model.earningCoefficient;
              const formatteProfits = this.decimalPipe.transform( profits, '1.2-2' );

              productsOffers.push({
                productId: selectedProduct.id,
                offerId: offer.id,
                packetOfferIndex: i,
                profits: formatteProfits,
              });
            }
          }
        }
      }
    }
    return productsOffers;
  }
  // end submit block


  getOffersSwitch() {
    this.offersSelected = this.enableAllOffer
      ? this.allOffersListEnabled
      : this.offersIdsListByFbPage;
  }

  ngAfterViewChecked(): void {
    this.cdRef.detectChanges();
  }


  offers(): FormArray {
    return this.packetForm.get('offers') as FormArray;
  }

  models(offerIndex: number): FormArray {
    return this.offers().at(offerIndex).get('offerModels') as FormArray;
  }

  products(modelIndex: number, productIndex: number): FormArray {
    return this.models(modelIndex)
      .at(productIndex)
      .get('products') as FormArray;
  }

  // edit mode after get offer (1)
  addSelectedOffer(offer: Offer): void {
    console.log(offer);

    this.offers().push(
      this.fb.group({
        id: offer.id,
        name: offer.name,
        price: offer.price,
        offerModels: this.fb.array([]),
      })
    );
  }

  newOffer(): FormGroup {
    return this.fb.group({
      id: 0,
      name: '',
      price: 0,
      offerModels: this.fb.array([]),
    });
  }


  getElement(model: any, field: string, field2: string) {
    return model[field] != null ? model[field][field2] : null;
  }

  addOffer(): void {
    this.offers().push(this.newOffer());
  }

  addModel(modelIndex: number,model:Model,selectedProduct:Product): void {
    this.models(modelIndex).push(this.newModel(model,selectedProduct));
  }

  newModel(model: Model,selectedProduct:Product): FormGroup {
    return this.fb.group({name: [model.name],
      colors: this.fb.array(model.colors ? model.colors.filter((color: Color) => color.name != "?").map(color => this.fb.control(color)) : []),
      sizes: this.fb.array(model.sizes ? model.sizes.filter((size: any) => size.reference != "?").map(size => this.fb.control(size)) : []),
      purchasePrice: [model.purchasePrice],
      earningCoefficient: [model.earningCoefficient],
      selectedColor: [selectedProduct.color],
      selectedSize: [selectedProduct.size],
      selectedProduct: [selectedProduct],
      selectedSizeReel: [model.selectedSizeReel]
    });
  }

  addProductToPacketDescription(
    modelName?: any,
    color?: any,
    size?: any,
    fakeSize?: any
  ) {
    //console.log('fakeSize',fakeSize);
    if (this.packetDescription.length > 0) this.packetDescription += ' , ';
    if (modelName != null) this.packetDescription += modelName;
    if (color != null && color != '?') this.packetDescription += ' ' + color;
    if (fakeSize != null) this.packetDescription += ' (' + fakeSize + ')';
    else if (size != null && size != '?')
      this.packetDescription += ' (' + size + ')';
  }

  calculateProductsPrice() {
    this.productsPrice = 0;
    for (let i = 0; i < this.offers().length; i++) {
      this.productsPrice += this.offers().at(i).get('price')?.value;
    }
    this.calculatePacketPrice();
  }

  calculatePacketPrice() {
    this.packetPrice =
      this.productsPrice +
      this.packetForm.controls['deliveryPrice'].value -
      this.packetForm.controls['discount'].value;
  }

  enableFake() {
    this.enableFakeSize = !this.enableFakeSize;
  }

  ngOnDestroy(): void {
    console.log('$unsubscribe');
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
