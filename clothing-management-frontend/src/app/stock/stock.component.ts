import { Component, OnInit } from '@angular/core';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ProductService } from '../services/product.service';

@Component({
  selector: 'app-stock',
  templateUrl: './stock.component.html',
  styleUrls: ['./stock.component.scss']
})
export class StockComponent implements OnInit {

  products: any[] = [];
  cols: any[] = [];
  selectedProducts: any[] = [];
  constructor(private productService: ProductService, private messageService: MessageService,private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.productService.findAllProducts()
    .subscribe((result: any) => {
      this.products = result;
    });
    
    this.cols = [
      { field: 'id', header: 'Id' },
      { field: 'date', header: 'Date' },
      { field: 'quantity', header: 'Quantité'},
      { field: 'reference', header: 'Référence' },
      { field: 'model', header: 'Modèle' },
      { field: 'color', header: 'Couleur' },
      { field: 'size', header: 'Taille' }
    ];
  }

  onEditInit($event: any){

  }

  onEditComplete($event: any){
    
  }

  onEditCancel($event: any){
    
  }

  deleteSelectedProducts() {
    let selectedProductsId = this.selectedProducts.map((selectedProduct: any) => selectedProduct.id);
    console.log(selectedProductsId);

    this.confirmationService.confirm({
      message: 'Êtes-vous sûr de vouloir supprimer les commandes séléctionnées ?',
      header: 'Confirmation',
      icon: 'pi pi-exclamation-triangle',
      accept: () => {
        this.productService.deleteSelectedProducts(selectedProductsId)
          .subscribe((result: any) => {
            console.log("packets successfully deleted !");
            this.products = this.products.filter((product: any) => selectedProductsId.indexOf(product.id) == -1);
            this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'Les produits séléctionnés ont été supprimé avec succés', life: 1000 });
          })
      }
    });
  }
}
