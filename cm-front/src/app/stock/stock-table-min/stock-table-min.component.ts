import { Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { Table } from 'primeng/table';
import { Model } from 'src/shared/models/Model';
import { ProductsCount, SoldProduct } from 'src/shared/models/ProductCountDTO';
import { ProductService } from 'src/shared/services/product.service';

@Component({
  selector: 'app-stock-table-min',
  templateUrl: './stock-table-min.component.html',
  styleUrl: './stock-table-min.component.scss'
})
export class StockTableMinComponent implements OnInit,OnChanges{

  selectedColumns: any[] =[];
  totalTableValue: number;
  totalColumnArray: Record<string,number> = {};
  productsCount:  ProductsCount = {};
  sizes: number[] = [];
  colors: number[] = [];
  @ViewChild('dt') dt!: Table;
  @Input() selectedModel: Model;
  modelId: number;
  productsStockTable: any[];
  constructor(
    private productService: ProductService
  ) {}

  ngOnInit(): void {

  }
  ngOnChanges(changes: SimpleChanges): void {
    this.getStockByModelId(this.selectedModel.id!);
  }

  getStockByModelId(modelId: number) {
    this.productService
      .getStockQuantity(modelId)
      .subscribe((result: any) => {
        if(result){
          this.productsCount = result;
          this.colors = this.selectedModel.colors!;
          this.sizes = this.selectedModel.sizes!;
          console.log("colors",this.colors);
          console.log("sizes",this.sizes);
          console.log(this.productsCount);

          this.countTotalColumn();
          this.productsStockTable = this.getProductsCountArray();//get colors and sizes
        }
      });
  }

  countTotalColumn() {
    let total:number = 0;
    console.log("sizes2",this.sizes);

    for (let size of this.sizes) {
        let x = this.totalColumn(size);
        total += x;
    }
    this.totalTableValue = total;
    console.log("total",total);

  }

  //get colors and sizes
  getProductsCountArray(): any[] {
    return Object.keys(this.productsCount).map(key => {
      const colorId = Number(key);
      return {
        colorId: colorId,
        sizes: this.productsCount[colorId]
      };
    });
  }


  totalRow(colorId: number):number {
    let totRow = 0;
    for (let sizeId in this.productsCount[colorId]) {
        totRow += this.productsCount[colorId][sizeId]?.qte || 0;
    }
    return totRow;
  }

  totalColumn(size: number): number {
    let totColumn = 0;
    if (Object.keys(this.productsCount).length === 0) {
      return 0;
    }
    else for (let color in this.productsCount)
      {
        const product : SoldProduct= this.productsCount[color][size];
        if (product)
        {
          totColumn += product?.qte;
        }
      }
    return totColumn;
  }
}
