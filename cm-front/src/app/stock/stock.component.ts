import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { ModelService } from 'src/shared/services/model.service';
import { ProductService } from '../../shared/services/product.service';
import { Model } from 'src/shared/models/Model';
import { ProductHistoryService } from 'src/shared/services/product-history.service';
import { switchMap } from 'rxjs';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss'],
})
export class StockComponent implements OnInit {
  products: any[] = [];
  models: Model[] = [];
  productsHistory: any;
  selectedProducts: number[] = [];
  isRowSelectable = true;
  isMultiple = false;
  qte: number = 0;
  @ViewChild('dt') dt!: Table;
  @ViewChild('el') el!: ElementRef;
  sizes: any[] = [];
  selectAll: boolean = false;
  selectedModel: string = "";
  constructor(private productService: ProductService, private productHistoryService: ProductHistoryService, private modelService: ModelService, private messageService: MessageService, private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.modelService.findAllModels()
      .subscribe((result: any) => {
        this.models = result;
        this.selectedModel = this.models[0].id;
        this.getStockByModelId(this.models[0].id);
      });
      this.productHistoryService.findAll(this.selectedModel)
      .subscribe((result: any) =>  {
        //console.log(result);
        this.productsHistory = result;
      })
  }

  onCellClick(product: any, event: any, j: number) {
    if (product.quantity == null)
      if (this.selectedProducts.includes(this.products[j][1].id))
        for (var i = 1; i < this.products[j].length; i++)
          this.unSelectProduct(j, i, this.products[j][i].id);
      else
        for (var i = 1; i < this.products[j].length; i++)
          this.selectProduct(j, i, this.products[j][i].id);
    else {
      const i = this.products[j].findIndex(
        (item: { id: number }) => item.id === product.id
      );
      if (this.selectedProducts.includes(product.id))
        this.unSelectProduct(j, i, this.products[j][i].id);
      else this.selectProduct(j, i, this.products[j][i].id);
    }
    console.log(this.selectedProducts);
  }

  selectMultiple(c: any, $event: any, index: number) {
    if (this.isMultiple) this.onCellClick(c, $event, index);
  }

  handleSizeClick(index: number, size: any) {
    let i = index + 1;
    if (this.selectedProducts.includes(this.products[0][i].id))
      for (var j = 0; j < this.products.length; j++)
        this.unSelectProduct(j, i, this.products[j][i].id);
    else
      for (var j = 0; j < this.products.length; j++)
        this.selectProduct(j, i, this.products[j][i].id);
    console.log(this.selectedProducts);
  }

  getStockByModelId(modelId: number) {
    this.productService.getStock(modelId)
      .subscribe((result: any) => {
        this.products = result.productsByColor;
        this.sizes = result.sizes;
      });
  }

  onModelChange($event: any) {
    this.getStockByModelId($event);
    console.log(this.selectedModel);
    this.selectedModel = $event;
    this.selectedProducts = [];
  }

  add() {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    let productsQuantities = [];
    for (var i = 0; i < this.selectedProducts.length; i++) {
      const foundArray = this.products.find((subArray: any) =>
        subArray.some((item: any) => item.id == this.selectedProducts[i])
      );
      const rowIndex = this.products.indexOf(foundArray);
      const columnIndex = foundArray
        .map((el: any) => el.id)
        .indexOf(this.selectedProducts[i]);
      console.log(
        `Element ${this.selectedProducts[i]} found at row ${rowIndex} and column ${columnIndex}.`
      );
      if (this.products[rowIndex][columnIndex].quantity != undefined) {
        this.products[rowIndex][columnIndex].quantity += this.qte;
        rows[rowIndex].cells[columnIndex].style.backgroundColor =
          'rgb(152,251,152)';
      }
      productsQuantities.push({ id: this.selectedProducts[i], quantity: this.products[rowIndex][columnIndex].quantity, enteredQuantity: this.qte, reference : this.products[rowIndex][columnIndex].reference })
    }
    this.productService.addStock(productsQuantities).pipe(switchMap(() => {
      this.selectedProducts = [];
      this.selectAll = false;
      return this.productHistoryService.findAll(this.selectedModel)
    })).subscribe((result: any) => this.productsHistory = result);
  }

  selectAllProducts() {
    for (var j = 0; j < this.products.length; j++)
      for (var i = 1; i < this.products[j].length; i++)
        if (this.selectAll)
          this.selectProduct(j, i, this.products[j][i].id);
        else
          this.unSelectProduct(j, i, this.products[j][i].id);
  }

  selectProduct( row: number, column: number, productId: any) {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    if (!this.selectedProducts.some((selectedProduct) => selectedProduct === productId)) {
      rows[row].cells[column].style.backgroundColor = 'rgb(220, 231, 243)';
      this.selectedProducts.push(productId);
    }
  }
  unSelectProduct(row: number, column: number, productId: any) {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    rows[row].cells[column].style.backgroundColor = 'rgb(255, 255, 255)';
    this.selectedProducts.splice(this.selectedProducts.indexOf(productId), 1);
  }

  totalRow(sizes: any[]){
    let qtes : number[] = sizes.flatMap((item)=> item.quantity);
    qtes.splice(0,1);
    let totalQuantity = qtes.reduce((sum, quantity) => sum + quantity, 0);
    return totalQuantity;
  }

  totalColumn(j:number){
    let totalColumn =0;
    for (var i = 1; i < this.products[j].length; i++)
      totalColumn += this.products[j][i];
  }
}
