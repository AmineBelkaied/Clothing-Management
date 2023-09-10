import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ProductHistoryService } from 'src/shared/services/product-history.service';

@Component({
  selector: 'app-stock-history',
  templateUrl: './stock-history.component.html',
  styleUrls: ['./stock-history.component.scss']
})
export class StockHistoryComponent implements OnInit {

  @Input()
  productsHistory: any;

  @Input()
  modelId!: number;

  rangeDates: Date[] = [];

  searchField: string = "";

  selectedProductsHistory = [];

  currentPage: number = 0;

  @Output()
  deleteProductsHistoryEvent: EventEmitter<any> = new EventEmitter();

  constructor(private productHistoryService: ProductHistoryService) { }

  ngOnInit(): void { }

  onPageChange($event: any){
    this.currentPage = $event.page;
    console.log(this.currentPage);
    
    this.productHistoryService.findAllProductsHistory(this.modelId, $event.page, $event.rows, this.searchField,
      this.convertDateToString(this.rangeDates[0]) != null ? this.convertDateToString(this.rangeDates[0]) : null , this.rangeDates[1] != null ? this.convertDateToString(this.rangeDates[1]) : null)
    .subscribe((result: any) => this.productsHistory = result)
  }

  displayQuantity(quantity: number) {
   return quantity > 0 ? "+" + quantity : quantity;
  }

  convertDateToString(date: Date) {
    if(date != null) {
      let day = date.getDate();
      let month = date.getMonth() + 1; // add 1 because months are indexed from 0
      let year = date.getFullYear();
      return year + "-" + month + "-" + day;
    }
    return;
  }

  deleteSelectedProductsHistory() {
    this.productHistoryService.deleteProductsHistory(this.selectedProductsHistory, this.modelId, this.currentPage)
    .subscribe((result: any) => {
      this.productsHistory = result;
      this.deleteProductsHistoryEvent.emit({products: this.selectedProductsHistory});
      this.selectedProductsHistory = [];
    })
  }
}
