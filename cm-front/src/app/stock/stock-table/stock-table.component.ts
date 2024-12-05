import { Component, Input, OnInit, ViewChild, OnChanges, SimpleChanges } from '@angular/core';
import { MessageService } from 'primeng/api';
import { Table } from 'primeng/table';
import { Model } from 'src/shared/models/Model';
import { ProductsCount, SoldProduct } from 'src/shared/models/ProductCountDTO';
import { ProductHistoryService } from 'src/shared/services/product-history.service';
import { ProductService } from 'src/shared/services/product.service';

@Component({
  selector: 'app-stock-table',
  templateUrl: './stock-table.component.html',
  styleUrl: './stock-table.component.scss'
})
export class StockTableComponent implements OnInit, OnChanges{

  selectedColumns: any[] =[];
  totalTableValue: { all: number, progress: number };
  totalColumnArray: Record<string, { all: number, progress: number }> = {};
  stockFabricationDelait: number = 30;
  stock: boolean = true;


  searchField: string = '';
  productsCount:  ProductsCount = {};
  addEnabled: boolean = true;
  sizes: number[] = [];
  productsHistory: any;
  selectedProducts: number[] = [];
  editedProducts: number[] = [];
  colors: number[] = [];
  selectAll: boolean = false;
  qte: number = 0;
  comment: string;
  @ViewChild('dt') dt!: Table;
  @Input() beginDateString: string;
  @Input() endDateString: string;
  @Input() selectedModel: Model;
  @Input() datesCount: any;
  enablerHistoryOptions: any[] = [
    { label: 'Off', value: false },
    { label: 'On', value: true },
  ];
  historyEnabler: boolean = false;
  modelId: number;
  productsStockTable: any[];
  tableOptions: { name: string; label: string; }[];
  selectedTableOptions: { name: string; label: string; }[];
  optionFlags: { [key: string]: boolean } = {};
  edit: boolean = true;
  page: number = 0;
  size: number = 10;
  constructor(
    private productService: ProductService,
    private productHistoryService: ProductHistoryService,
    private messageService: MessageService
  ) {}

  isOptionsDialogVisible: boolean = false;
  showOptionsDialog() {
    this.isOptionsDialogVisible = true;
  }

  ngOnInit(): void {
  this.tableOptions = [
    { name: 'showNoStockColors', label: 'Couleurs vide' },
    { name: 'showNoStockSizes', label: 'Tailles vide' },
    { name: 'showProgress', label: 'En cours' },
    { name: 'showOOS', label: 'Rupture' },
    { name: 'showSeverity', label: 'Info stock' },
    { name: 'showExchange', label: 'Echange' },
    { name: 'delaiEnabled', label: 'Délai' },
    { name: 'Archivé', label: 'Archivé' }
  ];



  this.selectedTableOptions = [
    { name: 'showNoStockColors', label: 'couleurs vide' },
    { name: 'showSeverity', label: 'Info stock' },
    { name: 'showNoStockSizes', label: 'Tailles vide' }
  ];

  this.tableOptions.forEach(option => {
    this.optionFlags[option.name] = this.selectedTableOptions.some(
      selected => selected.name === option.name
    );
  });
}

  onOptionSelect() {
    this.tableOptions.forEach(option => {
      this.optionFlags[option.name] = this.selectedTableOptions.some(
        selected => selected.name === option.name
      );
    });
  }

  ngOnChanges(simpleChanges: SimpleChanges){
    console.log("simpleChanges",simpleChanges);
    this.selectedProducts = [];

    if(simpleChanges['selectedModel'] && this.selectedModel != null )
      {
        this.historyEnabler = false;
        this.selectedModel = simpleChanges['selectedModel'].currentValue;
        this.modelId = this.selectedModel.id!;
        this.getStockByModelId(this.modelId);
      }
    else if(simpleChanges['endDateString'] && simpleChanges['endDateString'].firstChange == false)
        {

          this.getStockByModelId(this.modelId);
          this.historyEnablerChange();
        }
  }

  handleColorClick(colorId: number) {
    if (!this.optionFlags['showNoStockColors'] || !this.optionFlags['showNoStockSizes']) {
      this.activateShowNullStock()
    } else {
      let haveSelectedItems = false;
      if (this.haveSelectedItems(colorId, true)) {
        haveSelectedItems = true;
      }
      if (this.productsCount[colorId]) {
        for (let size in this.productsCount[colorId]) {
          const product : SoldProduct= this.productsCount[colorId][size];
          if (product) {
            if (haveSelectedItems) {
              this.unSelectProduct(product?.id);
            } else {
              this.selectProduct(product?.id);
            }
          }
        }
      }
    }
  }

  handleSizeClick(size: number): void {
    if (!this.optionFlags['showNoStockColors'] || !this.optionFlags['showNoStockSizes']) {
      this.activateShowNullStock()
    }else {
      let haveSelectedItems = false;
      if (this.haveSelectedItems(size, false)) haveSelectedItems = true;
      for (let color in this.productsCount) {
        const product : SoldProduct= this.productsCount[color][size];
        if (haveSelectedItems)
          this.unSelectProduct(product.id);
        else this.selectProduct(product.id);
      }
    }
  }

  countTotalColumn() {
    let total: { all: number; progress: number } = { all: 0, progress: 0 };

    for (let size of this.sizes) {
        let x = this.totalColumn(size);
        this.totalColumnArray[size] = x;
        total.all += x.all;
        total.progress += x.progress;
    }
    this.totalTableValue = total;
  }

  hideNoStockSizesChanged() {
    this.selectedColumns = [];
    if (this.optionFlags['showNoStockSizes']) {
        this.selectedColumns = this.selectedModel.sizes;
    }
    else {
      for (let size of Object.keys(this.totalColumnArray)) {
        if (this.totalColumnArray[size].all > 0) {
          this.selectedColumns.push(size);
        }
      }
    }
  }

  onCellClick(product: any): void {
    if (!this.optionFlags['showNoStockColors'] || !this.optionFlags['showNoStockSizes']) {
      this.activateShowNullStock();
    }else {
      if (this.selectedProducts.includes(product))
        this.unSelectProduct(product);
      else this.selectProduct(product);
    }
  }

  getStockByModelId(modelId: number) {
    this.productService
      .getStock(modelId, this.beginDateString, this.endDateString)
      .subscribe((result: any) => {
        if(result.model){
          this.productsCount = result.productsByColor;
          this.colors = result.model.colors!;
          this.sizes = result.model.sizes!;
          this.countTotalColumn();
          this.hideNoStockSizesChanged();
          this.productsStockTable = this.getProductsCountArray()
        }
      });
  }

  getProductsCountArray(): any[] {
    return Object.keys(this.productsCount).map(key => {
      const colorId = Number(key);
      return {
        colorId: colorId,
        sizes: this.productsCount[colorId]
      };
    });
  }

  activateShowNullStock(){
    this.optionFlags['showNoStockColors'] = true;
    this.optionFlags['showNoStockSizes'] = true;
  }

  selectProduct(productId: any) {
    if (!this.selectedProducts.includes(productId)) {
      this.selectedProducts.push(productId);
    }
  }

  unSelectProduct(productId: any) {
    const index = this.selectedProducts.indexOf(productId);
    if (index > -1) {
      this.selectedProducts.splice(index, 1);
    }
    const indexEdit = this.editedProducts.indexOf(productId);
    if (index > -1) {
      this.editedProducts.splice(indexEdit, 1);
    }
  }

  isProductSelected(productId: any): boolean {
    return this.selectedProducts.includes(productId);
  }

  isProductEdited(productId: any): boolean {
    return this.editedProducts.includes(productId);
  }

  selectAllProducts() {
    for (let colorId in this.productsCount)
      for (let size in this.productsCount[colorId])
      {
        const product : SoldProduct= this.productsCount[colorId][size];
        if (this.selectAll) this.selectProduct(product.id);
        else this.unSelectProduct(product.id);
      }
  }

  haveSelectedItems(index: number, row: boolean): boolean {
    if (row) {
      for (let sizeId in this.productsCount[index])
        if (this.selectedProducts.includes(this.productsCount[index][sizeId].id)) {
          return true;
        }
    } else
      for (let colorId in this.productsCount)
        if (this.selectedProducts.includes(this.productsCount[colorId][index].id)) {
          return true;
        }
    return false;
  }

  onDeleteProductsHistory($event: any): void {
    $event.products.forEach((product: any) => {
      for (let color in this.productsCount)
        for (let size in this.productsCount[color])
          if (product.productId == this.productsCount[color][size].id)
            this.productsCount[color][size].qte =
              this.productsCount[color][size].qte - product.quantity;
    });
  }
  onHistoryPageChange($event: any): void {
    this.page = $event;
    this.getProductHistory();
  }

  getSeverity(qte: number, delait: any) {
    switch (true) {
      case qte < 1:
        return 'danger';
      case delait < this.stockFabricationDelait:
        return 'warning';
      case delait > 60:
        return 'info';
      default:
        return 'success';
    }
  }
  getSeverityMsg(qte: number, delait: any) {
    switch (true) {
      case qte < 1:
        return 'RUPTURE';
      case delait < this.stockFabricationDelait:
        return 'LOW STOCK';
      case delait > 60:
        return 'OVER STOCK';
      default:
        return 'EN STOCK';
    }
  }

  totalRow(colorId: number) {
    let totRow = 0;
    let totProgressRow = 0;
    for (let sizeId in this.productsCount[colorId]) {
        if (this.stock) {
            totRow += this.productsCount[colorId][sizeId]?.qte || 0;
        } else {
            totRow += this.productsCount[colorId][sizeId]?.countPaid || 0;
        }
        totProgressRow += this.productsCount[colorId][sizeId]?.countProgress || 0;
    }
    return { all: totRow, progress: totProgressRow };
  }

  totalColumn(size: number):{ all: number; progress: number; } {
    let totColumn = 0;
    let progress = 0;
    if (Object.keys(this.productsCount).length === 0) {
      return { all: 0, progress: 0 };
    }
    else for (let color in this.productsCount)
      {
        const product : SoldProduct= this.productsCount[color][size];
        if (product)
        {
          if (this.stock) {
            totColumn += product?.qte;
          } else totColumn += product?.countPaid;
          progress += product?.countProgress;
        }
      }
    return {'all':totColumn,'progress':progress};
  }

  add() {
    let rows = this.dt.el.nativeElement.querySelectorAll('tbody tr');
    if (this.addEnabled) {
      this.addEnabled = false;
      if (this.qte == 0) {
        this.messageService.add({
          severity: 'warn',
          summary: 'Warning Message',
          detail: 'Quantité 0',
        });
        this.addEnabled = true;
        return;
      } else if (this.comment == '' || this.comment == null) {
        this.messageService.add({
          severity: 'warn',
          summary: 'Warning Message',
          detail: 'Commentaire vide',
        });
        this.addEnabled = true;
        return;
      } else if (this.selectedProducts.length < 1) {
        this.messageService.add({
          severity: 'warn',
          summary: 'Warning Message',
          detail: 'Aucun élément sélectionné',
        });
        this.addEnabled = true;
        return;
      } else
        this.productService
          .addStock(
            this.selectedProducts,
            this.qte,
            +this.modelId,
            this.comment
          )
          .subscribe({
            next: (result: any) => {
              // Successful response handling
              for (let colorId in this.productsCount) {
                for (let size in this.productsCount[colorId]) {
                  const product : SoldProduct= this.productsCount[colorId][size];
                  if (
                    this.selectedProducts.includes(product?.id)
                  ) {
                    this.productsCount[colorId][size].qte += this.qte;
                  }
                }
              }
              this.productsHistory = result;
              this.editedProducts = this.selectedProducts;
              this.selectedProducts = [];
              this.selectAll = false;
              this.messageService.add({
                severity: 'success',
                summary: 'Success',
                detail: 'Le stock a été ajusté avec succès',
              });
            },
            error: (error) => {
              // Error handling
              this.addEnabled = true;
              this.messageService.add({
                severity: 'error',
                summary: 'Error',
                detail: "Une erreur est survenue lors de l'ajustement du stock",
              });
              console.error('Error adjusting stock:', error);
            },
            complete: () => {
              this.addEnabled = true;
            },
          });
    }
  }

  getDaysStock(productSize: any): number {
    if(productSize.id == 722){
      console.log(productSize);
      console.log("change",this.datesCount);
    }

    let countPaid = productSize.countPaid;
    let qte = productSize.qte;
    if (countPaid === 0) countPaid = 1;
    let dayStock = qte / (countPaid / this.datesCount);
    return Number(dayStock.toFixed(1));
  }

  getProductHistory() {
    this.productHistoryService
      .findAll(
        this.modelId,
        this.page,
        this.searchField,
        this.beginDateString,
        this.endDateString
      )
      .subscribe((result: any) => {
        this.productsHistory = result;
      });
  }


  historyEnablerChange() {
    if (this.historyEnabler) this.getProductHistory();
  }

  onInputChange(): void {
    // Check if the search field is empty
    if (!this.searchField || this.searchField.trim() === '') {
      this.getProductHistory();
    }
  }
}
