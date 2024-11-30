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

  @Input() modelId!: number;

  rangeDates: Date[] = [];
  searchField: string = "";
  selectedProductsHistory: any[] = [];
  currentPage: number = 0;

  @Output() deleteProductsHistoryEvent: EventEmitter<any> = new EventEmitter();
  @Output() currentPageChange: EventEmitter<any> = new EventEmitter();

  constructor(private productHistoryService: ProductHistoryService) { }

  ngOnInit(): void { }

  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.currentPageChange.emit($event.page);
    //this.loadProductsHistory($event.page, $event.rows);
  }

  displayQuantity(quantity: number): string {
    return quantity > 0 ? `+${quantity}` : `${quantity}`;
  }

  convertDateToString(date: Date): string | null {
    return date ? `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` : null;
  }

  deleteSelectedProductsHistory(): void {
    console.log(this.selectedProductsHistory);

    this.productHistoryService.deleteProductsHistory(
      this.selectedProductsHistory,
      this.modelId,
      this.currentPage
    ).subscribe((result: any) => {
      this.productsHistory = result;
      this.deleteProductsHistoryEvent.emit({ products: this.selectedProductsHistory });
      this.selectedProductsHistory = [];
    });
  }
}