import { Component, Input, OnInit } from '@angular/core';
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
  modelId!: string;

  rangeDates: any;

  constructor(private productHistoryService: ProductHistoryService) { }

  ngOnInit(): void {
    console.log(this.productsHistory);
    
  }

  onPageChange($event: any){

    console.log($event);
    
    this.productHistoryService.findAllProductsHistory(this.modelId, $event.page, $event.rows)
    .subscribe((result: any) => this.productsHistory = result)
    
    
  }

  displayQuantity(quantity: number) {
   return quantity > 0 ? "+" + quantity : quantity;
  }
  
  filterChange(event:any) {
    console.log(this.rangeDates);
    
    this.productHistoryService.findAll(this.modelId, this.rangeDates[0], this.rangeDates[1] != null ? this.rangeDates[1] : null)
    .subscribe((result: any) => this.productsHistory = result)
  }
  
}
