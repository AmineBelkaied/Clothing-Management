import { Component, Input, OnInit, OnDestroy, Output, EventEmitter } from '@angular/core';
import { Subject, takeUntil } from 'rxjs';
import { Color } from 'src/shared/models/Color';
import { Model } from 'src/shared/models/Model';
import { Size } from 'src/shared/models/Size';
import { ColorService } from 'src/shared/services/color.service';
import { ModelService } from 'src/shared/services/model.service';
import { SizeService } from 'src/shared/services/size.service';

@Component({
  selector: 'app-add-model',
  templateUrl: './add-model.component.html'
})
export class AddModelComponent implements OnInit, OnDestroy {

  salePrice: number = 1;
  @Input()
  model: Model;
  @Input()
  editMode!: boolean;
  @Input()
  modelNameExists: boolean;
  @Output()
  formValidationEmitter = new EventEmitter();

  colors: Color[] = [];
  sizes: Size[] = [];
  currentFile: any;
  progress = 0;
  message: string;
  allOffersList: any[] = [];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private modelService: ModelService,
    private colorService: ColorService,
    private sizeService: SizeService) {

  }

  ngOnInit(): void {
    this.model.colors = this.model.colors.filter((color: Color) => color.name != "?");
    this.model.sizes = this.model.sizes.filter((size: any) => size.reference != "?");
    this.salePrice = this.calculateSalePrice(this.model);

    this.colorService.getColorsSubscriber().pipe(takeUntil(this.$unsubscribe))
      .subscribe((colorList: Color[]) => {
        this.colors = colorList.filter((color: Color) => color.name != "?");
      });

    this.sizeService.getSizesSubscriber().pipe(takeUntil(this.$unsubscribe))
      .subscribe((sizeList: Size[]) => {
        this.sizes = sizeList.filter((size: Size) => size.reference != "?");
      })
  }

  checkFormValidation(): void {
    this.formValidationEmitter.next(this.salePrice);
  }

  calculateSalePrice(model: Model): any {
    this.checkFormValidation();
    if (model.purchasePrice && model.earningCoefficient)
      return Math.round(model.earningCoefficient * model.purchasePrice);
  }

  calculateGainCoefficient(model: Model): void {
    this.checkFormValidation();
    if (model.purchasePrice && this.salePrice) {
      let gc = this.salePrice / model.purchasePrice;
      model.earningCoefficient = parseFloat(gc.toFixed(2))
    } else {
      model.earningCoefficient = 0;
    }
  }

  ngOnDestroy(): void {
    this.model = {...this.modelService.defaultModel};
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
