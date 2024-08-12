import { Component, Input, OnInit, OnDestroy} from '@angular/core';
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



export class AddModelComponent implements OnInit,OnDestroy{
  salePrice : number = 1;
  model: Model;
  @Input() editMode!: boolean;
  colors: Color[] = [];
  sizes: Size[] = [];
  currentFile: any;
  progress = 0;
  message : any;
  allOffersList: any[] = [];
  $unsubscribe: Subject<void> = new Subject();

  constructor(
    private modelService : ModelService,
    private colorService : ColorService,
    private sizeService : SizeService) {

  }

  ngOnInit(): void {

    this.modelService.getModelSubscriber()
    .pipe(takeUntil(this.$unsubscribe))
    .subscribe((model: Model) => {
      this.model = model;
      this.model.colors = this.model.colors.filter((color: Color) => color.reference != "?");
      this.model.sizes = this.model.sizes.filter((size: any) => size.reference != "?");
      console.log(this.model);
      this.salePrice = this.calculateSalePrice(this.model);
    });

    this.colorService.getColorsSubscriber().pipe(takeUntil(this.$unsubscribe))
    .subscribe((colorList: Color[]) => {
      console.log("colorList",colorList);
      this.colors = colorList.filter((color: Color) => color.reference != "?");
    });

    this.sizeService.getSizesSubscriber().pipe(takeUntil(this.$unsubscribe))
      .subscribe((sizeList: Size[]) => {
        this.sizes = sizeList.filter((size: any) => size.reference != "?");
      })

  }

  calculateSalePrice(model: Model): any {
    if(model.purchasePrice && model.earningCoefficient)
      return Math.round(model.earningCoefficient * model.purchasePrice);
  }

  calculateGainCoefficient(model: Model): void {
    if (model.purchasePrice && this.salePrice) {
      let gc = this.salePrice / model.purchasePrice;
      model.earningCoefficient = parseFloat(gc.toFixed(2))
    } else {
      model.earningCoefficient = 0;
    }
  }

  ngOnDestroy(): void {
    this.modelService.cleanModel();
    this.$unsubscribe.next();
    this.$unsubscribe.complete();
  }
}
