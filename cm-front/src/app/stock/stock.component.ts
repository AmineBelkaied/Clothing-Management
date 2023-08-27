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
  selectedModel: string = '';
  hide0 : boolean = false;

  rangeDates: Date[] = [];

  searchField: string = '';
  constructor(
    private productService: ProductService,
    private productHistoryService: ProductHistoryService,
    private modelService: ModelService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.modelService.findAllModels().subscribe((result: any) => {
      this.models = result;
      this.selectedModel = this.models[4].id;
      this.getStockByModelId(this.models[4].id);
      this.productHistoryService
        .findAll(this.selectedModel)
        .subscribe((result: any) => {
          console.log(result);
          this.productsHistory = result;
        });
    });
  }

  getStockByModelId(modelId: number) {
    this.productService.getStock(modelId).subscribe((result: any) => {
      this.products = result.productsByColor;
      console.log(this.products);

      this.sizes = result.sizes;
    });
  }

  onModelChange($event: any) {
    this.getStockByModelId($event);
    console.log(this.selectedModel);
    this.selectedModel = $event;
    this.selectedProducts = [];
  }

  onCellClick(product: any, event: any, j: number) :void{
    const i = this.products[j].findIndex(
      (item: { id: number }) => item.id === product.id
    );
    if (this.selectedProducts.includes(product.id))
      this.unSelectProduct(j, i, this.products[j][i].id);
    else this.selectProduct(j, i, this.products[j][i].id);
    console.log(this.selectedProducts);
  }

  handleColorClick(j: number) {
    if (this.haveSimilar(j, true))
      for (var i = 0; i < this.products[j].length; i++)
        this.unSelectProduct(j, i, this.products[j][i].id);
    else
      for (var i = 0; i < this.products[j].length; i++)
        this.selectProduct(j, i, this.products[j][i].id);
    //console.log('selectedProducts',this.selectedProducts);
  }

  handleSizeClick(i: number) :void{
    if (this.haveSimilar(i, false))
      for (var j = 0; j < this.products.length; j++)
        this.unSelectProduct(j, i, this.products[j][i].id);
    else
      for (var j = 0; j < this.products.length; j++)
        this.selectProduct(j, i, this.products[j][i].id);
    //console.log('selectedProducts',this.selectedProducts);
  }

  selectProduct(j: number, i: number, productId: any) {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
      //console.log('select row', j+' column '+i);
      rows[j].cells[i + 1].style.backgroundColor = 'rgb(220, 231, 243)';
      this.selectedProducts.push(productId);
  }
  unSelectProduct(j: number, i: number, productId: any) {
    //console.log('unSelect row', j+' column '+i);
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    rows[j].cells[i + 1].setAttribute('style','cursor:pointer');//.style.backgroundColor = 'rgb(255, 255, 255)';
    let index =this.selectedProducts.indexOf(productId);
    if(index>-1) this.selectedProducts.splice(index, 1);
    //console.log('this.selectedProducts after unselect',this.selectedProducts+' unselected '+productId+' index '+index);
  }

  selectAllProducts() {
    for (var j = 0; j < this.products.length; j++)
      for (var i = 0; i < this.products[j].length; i++)
        if (this.selectAll) this.selectProduct(j, i, this.products[j][i].id);
        else this.unSelectProduct(j, i, this.products[j][i].id);
  }

  totalRow(j: number) {
    let totRow = 0;
    for (var i = 0; i < this.products[j].length; i++)
      totRow += this.products[j][i].quantity;
    return totRow;
  }

  totalColumn(i: number) {
    let totColumn = 0;
    for (var j = 0; j < this.products.length; j++)
      totColumn += this.products[j][i].quantity;
    return totColumn;
  }

  totalTable() {
    let tot = 0;
    for (var j = 0; j < this.products.length; j++)
      for (var i = 0; i < this.products[j].length; i++)
        tot += this.products[j][i].quantity;
    return tot;
  }

  add() {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    this.productService
      .addStock(this.selectedProducts, this.qte, +this.selectedModel)
      .subscribe((result: any) => {
        console.log('result', result);

        for (var j = 0; j < this.products.length; j++)
          for (var i = 0; i < this.products[j].length; i++)
            if (this.selectedProducts.includes(this.products[j][i].id)) {
              //console.log('prod', this.products[j][i].color.name);
              this.products[j][i].quantity += this.qte;
              rows[j].cells[i + 1].setAttribute(
                'style',
                'background-color: rgb(152, 251, 152);'
              ); //.style.backgroundColor = 'rgb(152,251,152)';
            }
        this.productsHistory = result;
        this.selectedProducts = [];
        this.selectAll = false;
        this.messageService.add({
          severity: 'success',
          summary: 'Success',
          detail: 'Le stock a été modifié avec succés',
        });
      });
  }

  filterChange(event: any) {
    this.onClearCalendar();
    this.productHistoryService
      .findAll(
        this.selectedModel,
        this.searchField,
        this.convertDateToString(this.rangeDates[0]) != null
          ? this.convertDateToString(this.rangeDates[0])
          : null,
        this.rangeDates[1] != null
          ? this.convertDateToString(this.rangeDates[1])
          : null
      )
      .subscribe((result: any) => {
        this.productsHistory = result;
      });
  }

  onClearCalendar() {
    if (this.rangeDates == null) this.rangeDates = [];
  }

  search() {
    this.productHistoryService
      .findAll(
        this.selectedModel,
        this.searchField,
        this.convertDateToString(this.rangeDates[0]) != null
          ? this.convertDateToString(this.rangeDates[0])
          : null,
        this.rangeDates[1] != null
          ? this.convertDateToString(this.rangeDates[1])
          : null
      )
      .subscribe((result: any) => (this.productsHistory = result));
  }

  convertDateToString(date: Date) {
    if (date != null) {
      let day = date.getDate();
      let month = date.getMonth() + 1; // add 1 because months are indexed from 0
      let year = date.getFullYear();
      return year + '-' + month + '-' + day;
    }
    return;
  }

  haveSimilar(index: number, row: boolean):boolean {
    if (row) {
      for (var i = 0; i < this.products[index].length; i++)
        if (this.selectedProducts.includes(this.products[index][i].id)){
          console.log('have similar row',this.products[index][i].id);
          return true
        }
    } else
      for (var j = 0; j < this.products.length; j++)
        if (this.selectedProducts.includes(this.products[j][index].id)){
          console.log('have similar size',this.products[j][index].id);
          return true
        }
    return false; // No common items found
  }

  hideRow(j:number){
    if(this.totalRow(j)==0)
      this.products.splice(j,1);
  }

/*   hideColumn(i:number){
    console.log('hide column',i);

    if(this.totalColumn(i)==0)
    for (var j = 0; j < this.products.length; j++)
      this.products[j].splice(i,1);
  } */

  onDeleteProductsHistory($event: any): void {
    $event.products.forEach((product: any) => {
      for (var j = 0; j < this.products.length; j++)
        for (var i = 0; i < this.products[j].length; i++)
        if(product.productId == this.products[j][i].id)
          this.products[j][i].quantity = this.products[j][i].quantity - product.quantity;
    });
  }

/*   getStyle(quantity: any) {
    if (quantity < 10)
    return {
      'background-color':'#D7A4A3',
      cursor: 'pointer',
    };
    else
    return {
      cursor: 'pointer'
    };
  } */

  getSeverity(product: any,button:boolean) {
    if (button)
    switch (true) {
      case product.quantity <1:
        return 'danger';

        case product.quantity < 10:
            return 'warning';

        default:
            return 'success';
    }
    else switch (true) {
      case product.quantity <1:
        return 'RUPTURE';

        case product.quantity < 10:
            return 'LOW STOCK';

        default:
            return 'EN STOCK';
    }
}
}
