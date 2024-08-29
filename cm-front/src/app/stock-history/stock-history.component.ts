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
  selectedProductsHistory: any[] = [];
  currentPage: number = 0;

  @Output()
  deleteProductsHistoryEvent: EventEmitter<any> = new EventEmitter();

  constructor(private productHistoryService: ProductHistoryService) { }

  ngOnInit(): void { }

  onPageChange($event: any): void {
    this.currentPage = $event.page;
    this.loadProductsHistory($event.page, $event.rows);
  }

  loadProductsHistory(page: number, rows: number): void {
    const beginDate = this.convertDateToString(this.rangeDates[0]);
    const endDate = this.convertDateToString(this.rangeDates[1]);

    this.productHistoryService.findAllProductsHistory(
      this.modelId,
      page,
      rows,
      this.searchField,
      beginDate,
      endDate
    ).subscribe((result: any) => {
      this.productsHistory = result;
    });
  }

  displayQuantity(quantity: number): string {
    return quantity > 0 ? `+${quantity}` : `${quantity}`;
  }

  convertDateToString(date: Date): string | null {
    return date ? `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}` : null;
  }

  deleteSelectedProductsHistory(): void {
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
