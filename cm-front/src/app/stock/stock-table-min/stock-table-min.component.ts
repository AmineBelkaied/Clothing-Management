import { Component, Input, OnChanges, OnInit, SimpleChanges, ViewChild } from '@angular/core';
import { Table } from 'primeng/table';
import { Model } from 'src/shared/models/Model';
import { ProductService } from 'src/shared/services/product.service';

type ProductQuantity = { size: number; qte: number };
type ProductsStockTable = Record<number, ProductQuantity[]>;

@Component({
  selector: 'app-stock-table-min',
  templateUrl: './stock-table-min.component.html',
  styleUrls: ['./stock-table-min.component.scss'] // Fixed typo 'styleUrl'
})

export class StockTableMinComponent implements OnInit, OnChanges {
  selectedColumns: number[] = [];
  totalTableValue: number = 0;
  productsCount: any[] = [];
  sizes: number[] = [];
  @ViewChild('dt') dt!: Table;
  @Input() selectedModel: Model;

  productsStockArray: { colorId: number; quantities: ProductQuantity[]; sizes: Record<number, number | null> }[] = [];
  constructor(private productService: ProductService) {}

  ngOnInit(): void {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['selectedModel'] && changes['selectedModel'].currentValue) {
      this.getStockByModelId(this.selectedModel.id!);
    }
  }

  getStockByModelId(modelId: number) {
    this.productService.getStockQuantity(modelId).subscribe((result: any) => {
      if (result) {
        this.productsCount = result;
        this.sizes = this.selectedModel?.sizes || [];
        this.selectedColumns = this.sizes;
        this.productsStockArray = this.mapProductsByColor(this.productsCount);
      }
    });
  }

  mapProductsByColor(products: { id: number; color: number; size: number; qte: number }[]) {
    const productsStockTable: ProductsStockTable = {};
    let total: number = 0;
    products.forEach(product => {
      const { color, size, qte } = product;
      if (!productsStockTable[color]) {
        productsStockTable[color] = [];
      }
      total += qte;
      productsStockTable[color].push({ size, qte });
    });
    this.totalTableValue = total;
    return Object.entries(productsStockTable).map(([colorId, quantities]) => {
      const sizesMap = this.sizes.reduce((acc, size) => {
        const productSize = quantities.find(q => q.size === size);
        acc[size] = productSize ? productSize.qte : null;
        return acc;
      }, {} as Record<number, number | null>);

      return {
        colorId: Number(colorId),
        quantities: quantities,
        sizes: sizesMap,
      };
    });
  }

  totalRow(colorId: number): number {
    const quantities = this.productsStockArray.find(row => row.colorId === colorId)?.quantities || [];
    return quantities.reduce((total, quantity) => total + quantity.qte, 0);
  }

  totalColumn(size: number): number {
    return this.productsStockArray.reduce((total, row) => {
      const productSize = row.sizes[size];
      return total + (productSize || 0);
    }, 0);
  }
}
