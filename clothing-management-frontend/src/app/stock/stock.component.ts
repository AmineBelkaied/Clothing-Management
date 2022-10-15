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
  oldProduct: any;
  constructor(private productService: ProductService, private messageService: MessageService,private confirmationService: ConfirmationService) { }

  ngOnInit(): void {
    this.productService.findAllProducts()
    .subscribe((result: any) => {
      this.products = result;
    });
    
    this.cols = [
      { field: 'model', header: 'Modèle' },
      { field: 'id', header: 'Id' },
      { field: 'date', header: 'Dernière modification' },
      { field: 'quantity', header: 'Quantité'},
      { field: 'reference', header: 'Référence' },
      { field: 'color.name', header: 'Couleur' },
      { field: 'size.reference', header: 'Taille' }
    ];
  }

  onEditInit($event: any){
    this.oldProduct = Object.assign({}, $event.data);
  }

  onEditComplete($event: any) {
    if(this.oldProduct.quantity != $event.data.quantity) {
      console.log($event.data);
      $event.data.date = new Date();
        this.productService.updatProduct($event.data)
        .subscribe(result => {
          console.log("quantity successfully updated !");
          this.messageService.add({ severity: 'success', summary: 'Succés', detail: 'La qunatité a été modifié avec succés', life: 1000 });
        })
    }
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

  getColor(qte: any) : any{
    if (qte <= 3 && qte > 0) 
    return '.warning-product';
    if(qte == 0)
    return '.no-products';
  }
}
