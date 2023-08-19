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
  styleUrls: ['./stock.component.scss']
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
        console.log(result);
        this.productsHistory = result;
      })
  }

  onCellClick(product: any, event: any, index: number) {
    if (product.quantity == null) {
      const foundArray = this.products.find((subArray: any) =>
        subArray.some((item: any) => item.name == product.name)
      );
      let productsIds = foundArray.filter((product: any) => product.quantity != null).map((product: any) => product.id)
      if (productsIds.every((id: number) => this.selectedProducts.includes(id)))
        this.deleteItemsFromArray(productsIds);
      else
        this.selectedProducts.push(...productsIds);

      let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
      for (var i = 1; i < rows[index].cells.length; i++) {
        rows[index].cells[i].style.backgroundColor === "rgb(220, 231, 243)" ? rows[index].cells[i].style.backgroundColor = "rgb(255, 255, 255)" : rows[index].cells[i].style.backgroundColor = "rgb(220, 231, 243)";
      }
    } else {
      event.target.style.backgroundColor === "rgb(220, 231, 243)" ? event.target.style.backgroundColor = "rgb(255, 255, 255)" : event.target.style.backgroundColor = "rgb(220, 231, 243)";
      if (this.selectedProducts.includes(product.id)) {
        this.selectedProducts.splice(this.selectedProducts.indexOf(product.id), 1);
      }
      else {
        this.selectedProducts.push(product.id);
      }
    }
    console.log(this.selectedProducts);
  }

  deleteItemsFromArray(items: any): void {
    items.forEach((element: any) => {
      this.selectedProducts.splice(this.selectedProducts.indexOf(element), 1);
    });
  }

  selectMultiple(c: any, $event: any, index: number) {
    if (this.isMultiple)
      this.onCellClick(c, $event, index)
  }

  handleSizeClick(index: number, size: any) {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    for (var i = 0; i < rows.length; i++) {
      rows[i].cells[index + 1].style.backgroundColor === "rgb(220, 231, 243)" ? rows[i].cells[index + 1].style.backgroundColor = "rgb(255, 255, 255)" : rows[i].cells[index + 1].style.backgroundColor = "rgb(220, 231, 243)";
      if (this.selectedProducts.includes(this.products[i][index + 1].id)) {
        this.selectedProducts.splice(this.selectedProducts.indexOf(this.products[i][index + 1].id), 1);
      }
      else {
        this.selectedProducts.push(this.products[i][index + 1].id)
      }
    }
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
  }

  add() {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr')
    let productsQuantities = [];
    for (var i = 0; i < this.selectedProducts.length; i++) {
      const foundArray = this.products.find((subArray: any) =>
        subArray.some((item: any) => item.id == this.selectedProducts[i])
      );
      const rowIndex = this.products.indexOf(foundArray);
      const columnIndex = foundArray.map((el: any) => el.id).indexOf(this.selectedProducts[i]);
      console.log(`Element ${this.selectedProducts[i]} found at row ${rowIndex} and column ${columnIndex}.`);
      if (this.products[rowIndex][columnIndex].quantity != undefined) {
        this.products[rowIndex][columnIndex].quantity += this.qte;
        rows[rowIndex].cells[columnIndex].style.backgroundColor = "rgb(152,251,152)"
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
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    for (var i = 0; i < rows.length; i++) {
      for (var j = 1; j < rows[i].cells.length; j++) {
        if (this.selectAll) {
          rows[i].cells[j].style.backgroundColor = "rgb(220, 231, 243)";
          this.selectedProducts.push(this.products[i][j].id);
        } else {
          rows[i].cells[j].style.backgroundColor = "rgb(255, 255, 255)";
          this.selectedProducts = [];
        }
      }
    }
  }

}
