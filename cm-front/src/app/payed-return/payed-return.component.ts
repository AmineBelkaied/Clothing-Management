import { Component, OnInit } from '@angular/core';
import { ProductService } from 'src/shared/services/product.service';

@Component({
  selector: 'app-payed-return',
  templateUrl: './payed-return.component.html',
  styleUrls: ['./payed-return.component.scss']
})
export class PayedReturnComponent implements OnInit {
  text: string = ''; // Initialize with the provided text
  extractedBarcodes: string[] = [];
  type!: string;

  constructor(private productService: ProductService) {

  }

  ngOnInit(): void {
  }

  extractBarcodes() {
    const barcodeRegex = /\d{12}/g;
    this.extractedBarcodes = this.text.match(barcodeRegex) || [];
    console.log(this.extractedBarcodes);
    console.log(this.type);
    this.productService.updateStatus(this.extractedBarcodes,this.type)
      .subscribe((result: any) => {
        console.log('result', result);
      });
  }
}
