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
import { ConfirmationService, MessageService } from 'primeng/api';
import { OOS, NOT_CONFIRMED } from 'src/shared/utils/status-list';
import { Offer } from 'src/shared/models/Offer';
import { OfferService } from 'src/shared/services/offer.service';
import { Observable, Subject, takeUntil, tap } from 'rxjs';
//import { Product } from 'src/shared/models/Product';
import { ProductService } from 'src/shared/services/product.service';
import { DecimalPipe } from '@angular/common';
import { ProductResponse } from 'src/shared/models/ProductResponse';
import { ColorService } from 'src/shared/services/color.service';
import { SizeService } from 'src/shared/services/size.service';
import { FbPage } from 'src/shared/models/FbPage';
import { FbPageService } from 'src/shared/services/fb-page.service';
import { ModelService } from 'src/shared/services/model.service';

@Component({
  selector: 'app-add-packet',
  templateUrl: './add-packet.component.html',
  styleUrls: ['./add-packet.component.css'],
  providers: [ConfirmationService, MessageService],
})
export class AddPacketComponent implements OnInit {
  @Input() packet: any;
  @Input() oneSourceApp : boolean;
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
  stockAvailable: number;
  colorSizeChoosen: boolean = true;
  packetEarningCoefficient = 0;
  allProducts: ProductResponse[] = [];
  $unsubscribe: Subject<void> = new Subject();
  offersSelected: Offer[];
  allOffersListEnabled: Offer[];
  //productsList: ProductResponse[];
  modelIds: number[]=[];

  showStockDialog: boolean = false;
  selectedModel: Model= {
    id: 0, // Or any default value
    name: '',
    description: '',
    colors: [] = [],
    sizes: [] = [],
    products: [] = [],
    earningCoefficient: 2,
    purchasePrice: 15,
    deleted: false,
    enabled: false
  }

  constructor(
    private fb: FormBuilder,
    private cdRef: ChangeDetectorRef,
    private packetService: PacketService,
    private confirmationService: ConfirmationService,
    private messageService: MessageService,
    private offersService: OfferService,
    private productService: ProductService,
    private decimalPipe: DecimalPipe,
    private colorService: ColorService,
    private sizeService: SizeService,
    private fbPageService: FbPageService
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
      .subscribe((products: ProductResponse[]) => (this.allProducts = products));

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
                  .includes(this.packet.fbPageId!)
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
          this.productService.updateProducts(offer.products);
          let productIds = offer.productIds;

          let offerX: Offer = this.allOffersList.find(
            (offerW) => offerW.id == offer.id
          );

          this.addSelectedOffer(offerX);
          this.addSelectedModels(offerX.offerModels,productIds, index);
        });

        this.packetForm.controls['deliveryPrice'].setValue(
          this.packet.deliveryPrice
        );
        this.packetForm.controls['discount'].setValue(this.packet.discount);
        this.calculateProductsPrice();
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
  addSelectedModels(offerModels: any,products: number[], offerIndex: number): void {
    if (offerModels.length > 0)
      for (let j = 0; j < offerModels.length; j++) {
        if (products[j] != null) {
          this.pushModelToOffer(offerIndex, offerModels[j].model, products[j]);
        } else console.log('product', products[j] + ' introuvable');
      }
    else console.log('products est vide');
  }


  setOfferModelsValues(offerIndex: number, offer: Offer): void {
    this.setOfferControlValues(this.offers().at(offerIndex), offer);
    for (let i = 0; i < offer.offerModels.length; i++) {
      let selectedModel: Model = offer.offerModels[i].model;
      for (let j = 0; j < offer.offerModels[i].quantity; j++) {
          if(selectedModel.defaultId! > -1){
            this.pushModelToOffer(offerIndex, selectedModel, selectedModel.defaultId!);
          }
      }
    }
    this.calculateProductsPrice();
  }

  pushModelToOffer(offerIndex: number, model: Model, selectedProduct: number) {
    this.addModel(offerIndex, model, selectedProduct);
  }

  onOfferChange(offerId: number, index: number): void {
    this.models(index).clear();
    let offer: Offer = this.allOffersList.find((offer) => offer.id === offerId)!;
    if (offer) {
      if (offer.offerModels.length > 0) {
        this.loadOfferProducts(offer).subscribe({
          next: () => {
            this.setOfferModelsValues(index, offer);
          },
          error: (err) => {
            console.error('Error loading products:', err);
          }
        });
      }
    }
  }

  loadOfferProducts(offer: Offer): Observable<ProductResponse[]> {
    for (let i = 0; i < offer.offerModels.length; i++) {
      let selectedModel: Model = offer.offerModels[i].model;
      if (!this.modelIds.includes(selectedModel.id!)) {
        this.modelIds.push(selectedModel.id!);
      }
    }
    return this.loadProducts(); // Return the observable from loadProducts
  }


  loadProducts(): Observable<ProductResponse[]> {
    return this.productService.loadProductsByModels(this.modelIds);
  }

  getModelFromOffer(modelIndex: number,selectedOffer: AbstractControl):Model | undefined{
    let pos = 0;
    let model: Model | undefined;

    let offer: Offer | undefined = this.allOffersList.find(
      (offer) => offer.name === selectedOffer.get('name')?.value
    );
    if (!offer) {
      console.warn('Offer not found.');
      return undefined;
    }
    // Loop through the offer models to find the correct model
    for (let i = 0; i < offer.offerModels.length; i++) {
      for (let j = 0; j < offer.offerModels[i].quantity; j++) {
        if (pos === modelIndex) {
          model = offer.offerModels[i].model;
          break; // Exit the inner loop once the model is found
        }
        pos++;
      }
      if (model) break; // Exit the outer loop if model is found
    }
    return model
  }
  colorSizeChange(
    selectedModel: AbstractControl,
    modelIndex: number,
    selectedOffer: AbstractControl
  ): void {
    // Find the offer by matching the name
    let model: Model | undefined = this.getModelFromOffer(modelIndex,selectedOffer);
    // Log for debugging
    if (!model) {
      console.warn('Model is undefined, skipping product selection.');
      return;
    }

    // Get selected color and size from the form control
    let color: number = selectedModel.get('selectedColor')?.value || null;
    let size: number = selectedModel.get('selectedSize')?.value || null;
    // Log for debugging
    let selectedProduct: ProductResponse | null = null;
    // Conditional logic based on color and size selection
    if (color == null && size == null) {
      let selectedProductId = model?.defaultId!;//this.getNoChoiceColorSizeProduct(model);
        let product = this.findProduct(selectedProductId);
        if (product) {
          selectedModel.get('selectedProduct')?.setValue(product);
        }
        return;
    } else if (color === null) {
      selectedProduct = this.allProducts
        .filter(product => product.sizeId !== null && product.modelId === model?.id)
        .find((product: ProductResponse) =>
          product.colorId === null && product.sizeId === size
        ) || null;
    } else if (size === null) {
      selectedProduct = this.allProducts
        .filter(product => product.colorId !== null && product.modelId === model?.id)
        .find((product: ProductResponse) =>
          product.colorId === color && product.sizeId === null
        ) || null;
    } else {
      selectedProduct = this.allProducts
        .filter(product =>
          product.sizeId !== null &&
          product.colorId !== null &&
          product.modelId === model?.id
        )
        .find((product: ProductResponse) =>
          product.colorId === color && product.sizeId === size
        ) || null;
    }

    if (selectedProduct) {
        selectedModel.get('selectedProduct')?.setValue(selectedProduct);
    }
  }

  //end set data

  //start submit block
  onSubmit(event: Event) {
    console.log("111",event);

    let packetGainCoefficient = 0;
    let packetPurchasePrice = 0;
    let packetEarningCoefficient: number;
    let packet = this.packetForm.value;
    console.log("2222",this.packet);

    for (let i = 0; i < packet.offers.length; i++) {
      let offer: Offer = packet.offers[i];
      if (offer.id != null) {
        if (offer.offerModels.length > 0) {
          offer.offerModels.forEach((model: Model) => {
            if (model) {
              packetGainCoefficient += model.earningCoefficient;//=2.19+1.55+1.6
              packetPurchasePrice += model.purchasePrice;//32+22+30=84
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
    let gain = this.productsPrice - packetPurchasePrice;
    if(this.packet.exchangeId == null)
      gain -= packet.discount;
    //=223-7-(27+40+50)=216-117=99

    //117+7+84
    packetEarningCoefficient = gain / packetGainCoefficient;
    let productsOffers: ProductsPacket[] = this.prepareProductsOffers(
      packet,
      packetEarningCoefficient
    );
    console.log("stock", this.stockAvailable);

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
      status: status
    };
    this.packetService
      .addProductsToPacket(selectedProducts)
      .subscribe((packet: any) => {
        let result = { packet: packet, modelDialog: false };
        this.submitEvent.emit(result);
      });
  }

  prepareProductsOffers(
    packet: any,
    packetEarningCoefficient: number
  ): ProductsPacket[] {
    let productsOffers: ProductsPacket[] = [];
    this.packetDescription = '';
    this.stockAvailable = 200;
    this.colorSizeChoosen = true;

    for (let i = 0; i < packet.offers.length; i++) {
      let offer: Offer = packet.offers[i];
      if (offer.id != null) {
        if (offer.offerModels.length > 0) {
          for (let j = 0; j < offer.offerModels.length; j++) {
            let model: Model = offer.offerModels[j];
            let colorId = model.selectedProduct?.colorId;
            let sizeId = model.selectedProduct?.sizeId;

            //create packet description
            let color = this.colorService.getColorNameById(colorId!);
            let size = this.sizeService.getSizeNameById(sizeId!);
            let reelSize = this.sizeService.getSizeNameById(model.selectedSizeReel!);
            this.addProductToPacketDescription(
              offer.offerModels[j].name,
              color,
              size,
              reelSize
            );
            let selectedProduct = model.selectedProduct;
            if (selectedProduct !== undefined) {
              let qte = selectedProduct.qte;
              let colorSizeFalse: boolean =
                colorId == null || sizeId == null;
              let x: number = colorSizeFalse ? -1 : qte < 1 ? 0 : qte ?? 0;
              this.stockAvailable =
                x < this.stockAvailable ? x : this.stockAvailable;

              const profits = packetEarningCoefficient * model.earningCoefficient;
              const formatteProfits = this.decimalPipe.transform(
                profits,
                '1.2-2'
              );

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
    this.offersSelected = this.enableAllOffer || this.oneSourceApp
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

  addModel(modelIndex: number, model: Model, selectedProductId?: number): void {

    this.models(modelIndex).push(this.newModel(model, selectedProductId!));
  }
  findProduct(selectedProductId: number) : ProductResponse{
    return this.allProducts.find(
      (product: ProductResponse) =>
        product.id == selectedProductId
    )!;
  }
  newModel(model: Model, selectedProductId: number): FormGroup {
    const selectedProduct = this.findProduct(selectedProductId);
    let colors = this.getColors(model.colors);
    let sizes = this.getSizes(model.sizes);
    // Handling case where selectedProduct may be undefined or null

    if (!selectedProduct) {
        throw new Error(`Product with ID ${selectedProductId} not found.`);
    }

    const formControls = {
        id: [model.id],
        name: [model.name],
        colors: colors,
        sizes: sizes,
        purchasePrice: [model.purchasePrice],
        earningCoefficient: [model.earningCoefficient],
        selectedColor: [{
          value: selectedProduct.colorId,
          disabled: colors.length === 1  // Set initial disabled state here
        }],
        selectedSize: [{
          value: selectedProduct.sizeId,
          disabled: sizes.length === 1  // Set initial disabled state here
        }],
        selectedProduct: [selectedProduct],
        selectedSizeReel: [model.selectedSizeReel]
    };
    //if(colors.length == 1)
    //this.disableColorSelect(formControls);
    return this.fb.group(formControls);
  }



  // Abstracted method to get colors
  private getColors(colorIds: number[]): FormArray {
    let colors : Color[] = this.colorService.getColorByIds(colorIds)
      return this.fb.array(colors);
  }

  // Abstracted method to get sizes
  private getSizes(sizeIds: number[]): FormArray {
    return this.fb.array(this.sizeService.getSizesByIds(sizeIds));
  }

  addProductToPacketDescription(
    modelName: any,
    color?: String,
    size?: String,
    fakeSize?: String
  ) {
    if (this.packetDescription.length > 0) this.packetDescription += ' , ';
    if (modelName != null) this.packetDescription += modelName;
    if (color != "") this.packetDescription += ' ' + color;
    if (fakeSize != "") this.packetDescription += ' (' + fakeSize + ')';
    else if (size != "") this.packetDescription += ' (' + size + ')';
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

  openStockTable(modelIndex: number,selectedOffer: AbstractControl) {
    let model :Model | undefined = this.getModelFromOffer(modelIndex,selectedOffer)
    console.log("model",model);
    this.selectedModel = model!;
    this.showStockDialog = true;

  }

  closeStockDialog() {
    this.showStockDialog = false;
  }

  ngOnDestroy(): void {
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
